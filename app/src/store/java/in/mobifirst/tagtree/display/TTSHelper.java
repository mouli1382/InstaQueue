package in.mobifirst.tagtree.display;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TTSHelper {
    private static final TTSHelper _INSTANCE = new TTSHelper();
    private TextToSpeech tts;

    public static TTSHelper getInstance() {
        return _INSTANCE;
    }

    public void init(Context context) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization Failed!");
                }
            }
        });
    }


    public void speak(String text, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak("Your kind attention please", TextToSpeech.QUEUE_ADD, null, null);
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        } else {
            tts.speak("Your kind attention please", TextToSpeech.QUEUE_ADD, null);
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }

    public void destroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
