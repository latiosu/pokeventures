package networking;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import engine.Config;
import engine.Core;
import engine.UI;
import engine.structs.Message;
import engine.structs.TimeComparator;
import networking.packets.Packet03Chat;

import java.text.SimpleDateFormat;
import java.util.*;

public class ChatClient {

    private class MessageBuffer {

        private final int MAX_SIZE;
        private Deque<String> buffer;

        MessageBuffer() {
            MAX_SIZE = Config.MAX_CHAT_ROWS;
            buffer = new ArrayDeque<String>(MAX_SIZE);
        }

        /**
         * Will return the contents of buffer as a complete String
         * after inserting new message.
         * Note: Each message is already delimited by newline characters
         * due to the nature of input.
         */
        String handle(String msg) {
            if(buffer.size() == MAX_SIZE) {
                buffer.removeFirst();
            }
            buffer.addLast(msg);
            return getText();
        }

        private String getText() {
            String text = "";
            for(String s : buffer){
                text += s;
            }
            return text;
        }
    }

    private Core core;
    private UI ui;
    private SimpleDateFormat dateFormat;
    private Queue<Message> messages;
    private MessageBuffer buffer;

    // Part of UI
    private TextField chatField;
    private TextArea chatArea;
    private TextArea chatAreaHL;

    public ChatClient(Core core) {
        this.core = core;
        this.ui = core.getUI();
        this.dateFormat = new SimpleDateFormat(Config.DATE_FORMAT_CHAT);
        messages = new PriorityQueue<Message>(Config.MESSAGES_INIT, new TimeComparator());
        buffer = new MessageBuffer();

        initUI();
    }

    /* Initialize Chat UI */
    private void initUI() {
        // Text Field
        chatField = new TextField("", ui.getSkin(), "chat");
        float x = 0; // (Centered)=Config.GAME_WIDTH / 4f
        float y = 0;
        final float width = Config.VIEWPORT_WIDTH;
        float height = 30;
        chatField.setX(x);
        chatField.setY(y);
        chatField.setWidth(width);
        chatField.setHeight(height);
        chatField.setBounds(x, y, width, height);
        chatField.setVisible(false);
        chatField.setMaxLength(Config.MAX_MSG_LENGTH);
        chatField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                String trimmed = textField.getText().trim();
                switch (c) {
                    case '\r':
                        if(trimmed.length() == 0){ // Ignore input
                            return;
                        }
                        // ==== Send message to server here ====
                        registerMsg(core.getPlayers().getMainPlayer().getUsername(), trimmed);
                        showChat(false);
                        break;
                }
            }
        });
        ui.getStage().addActor(chatField);

        // Text Area Highlight (On when chatting)
        chatAreaHL = new TextArea("", ui.getSkin(), "chat-faded");
        chatAreaHL.setX(x);
        chatAreaHL.setY(30);
        height = 130;
        chatAreaHL.setWidth(width);
        chatAreaHL.setHeight(height);
        chatAreaHL.setDisabled(true);
        chatAreaHL.setVisible(false);
        chatAreaHL.setTouchable(Touchable.disabled);
        ui.getStage().addActor(chatAreaHL);

        // Text Area (Always on)
        chatArea = new TextArea("", ui.getSkin(), "chat-very-faded");
        chatArea.setX(x);
        chatArea.setY(30);
        chatArea.setWidth(width);
        chatArea.setHeight(height);
        chatArea.setPrefRows(Config.MAX_CHAT_ROWS);
        chatArea.setDisabled(true);
        chatArea.setVisible(true);
        chatArea.setTouchable(Touchable.disabled); // Should this really by untouchable??
        ui.getStage().addActor(chatArea);

        // Chat control listener
        ui.getStage().addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(keycode == Input.Keys.ESCAPE){
                    showChat(false);
                }
                return false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ui.showChat(chatField.hit(x - Config.VIEWPORT_WIDTH /2f, y, true) != null);
                return false;
            }
        });
    }

    private void updateChatUI(Message msg) {
        String formatted = String.format("(%s) %s: %s\n", getDate(msg.time), msg.username, msg.message);
        chatArea.setText(buffer.handle(formatted));
    }

    /**
     * Attempts to contact server to register the new message.
     * Note: The message will automatically be associated with
     * the time of creation.
     */
    public void registerMsg(String username, String msg) {
        core.registerMsg(new Packet03Chat(new Message(username, msg)));
    }

    /**
     * Stores message inside client's message bank and logs result if successful.
     * Note: Does NOT send a reply to the server.
     */
    public boolean storeMsg(Message msg) {
        if(!messages.contains(msg)){
            if(messages.add(msg)){
                updateChatUI(msg);
                return true;
            } else {
                System.err.printf("Error: Failed to store message %s", msg.message);
            }
        } else {
            System.err.printf("Error: Message already exists in message bank.");
        }
        return false;
    }

    /**
     * Note: Also updates UI focus based on given boolean value.
     */
    public void showChat(boolean b) {
        ui.setFocus(b);
        chatField.setText("");
        chatField.setVisible(b); // Show input area
        chatAreaHL.setVisible(b); // Highlight chat area
        if(b){
            ui.getStage().setKeyboardFocus(chatField);
        } else {
            ui.getStage().unfocusAll();
        }
    }

    private String getDate(long time) {
        return dateFormat.format(new Date(time));
    }
}
