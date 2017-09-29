package io.jeti.terminal;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import io.jeti.layoutparams.MatchMatch;
import io.jeti.layoutparams.MatchWrap0;
import io.jeti.layoutparams.MatchZero1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This {@link LinearLayout} acts link a UNIX terminal. You can enter commands,
 * clear the terminal, and view its output.
 */
public class Terminal extends LinearLayout {

    private TextView        terminal   = null;
    private EditText        editText   = null;
    private ScrollView      scrollView = null;
    public static final int BLUE       = Color.rgb(0, 124, 253);

    public Terminal(final Context context) {
        this(context, View.generateViewId());
    }

    /**
     * Use this constructor if you want the terminal to retain the output after
     * a rotation event.
     */
    public Terminal(final Context context, int terminalID) {
        super(context);

        setOrientation(VERTICAL);
        setLayoutParams(new MatchMatch());
        setBackgroundColor(Color.BLACK);

        scrollView = new ScrollView(context);
        addView(scrollView, new MatchZero1());

        terminal = new TextView(context);
        terminal.setId(terminalID);
        styleTerminal(terminal);
        scrollView.addView(terminal, new MatchMatch());

        editText = new EditText(context);
        editText.setHint("Enter command, e.g. 'netstat -lat'");
        styleEditText(editText);
        addView(editText, new MatchWrap0());

        Button sendButton = new Button(context);
        sendButton.setHint("Send Command");
        sendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null){
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }

                /*
                 * Get the command from the editText, add the command to the
                 * terminal, and clear it.
                 */
                String command = editText.getText().toString();
                String terminalText = terminal.getText().toString() + "\n$ " + command + "\n";
                terminal.setText(terminalText);
                editText.setText("");

                /* Try executing the command. */
                Process process;
                BufferedReader br = null;
                try {
                    process = Runtime.getRuntime().exec(command);
                    br = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    /* Get the results and update the textView */
                    StringBuilder log = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        log.append(line).append("\n");
                    }
                    terminal.setText(terminalText + log.toString() + "\n");
                } catch (IOException e) {
                    terminal.setText(terminalText + e.toString() + "\n");
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                /* Scroll to the end of the terminal. */
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
        styleSendButton(sendButton);
        addView(sendButton, new MatchWrap0());

        Button clearButton = new Button(context);
        clearButton.setHint("Clear Terminal");
        clearButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                terminal.setText("");
            }
        });
        styleClearButton(clearButton);
        addView(clearButton, new MatchWrap0());
    }

    /**
     * Style the Terminal's {@link android.widget.TextView}. This is put inside
     * of a {@link ScrollView}, and is called immediately before being added to
     * the layout.
     */
    public void styleTerminal(TextView textView) {
        textView.setTypeface(Typeface.MONOSPACE);
        textView.setTextColor(BLUE);
        textView.setBackgroundColor(Color.BLACK);
        // Glow.glow(terminal);
    }

    /**
     * Style the {@link android.widget.EditText} where you input commands. You
     * can override this call if you want. This method is called immediately
     * before being added to the layout.
     */
    public void styleEditText(EditText editText) {
    }

    /**
     * Style the Terminal's "Send Command" {@link android.widget.Button}. You
     * can override this call if you want. This method is called immediately
     * before being added to the layout.
     */
    public void styleSendButton(Button button) {
    }

    /**
     * Style the Terminal's "Clear Terminal" {@link android.widget.Button}. You
     * can override this call if you want. This method is called immediately
     * before being added to the layout.
     */
    public void styleClearButton(Button button) {
    }

}
