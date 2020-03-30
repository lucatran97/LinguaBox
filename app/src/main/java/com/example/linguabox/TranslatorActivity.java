package com.example.linguabox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.example.linguabox.HttpRequest.publicPostGetString;

public class TranslatorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner lan_list_1, lan_list_2;
    ArrayAdapter lan_Adapter;
    EditText input;
    TextView output;
    Button translateButton;
    String inputText;
    String language_from, language_from_code, language_to, language_to_code;
    String translation, translationString;
    private ExecutorService es;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translator_machine);
        lan_list_1 = findViewById(R.id.language_list_1);
        lan_list_2 = findViewById(R.id.language_list_2);

        es = Executors.newSingleThreadExecutor();

        lan_Adapter = ArrayAdapter.createFromResource(this, R.array.language_list, android.R.layout.simple_spinner_item);
        lan_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        lan_list_1.setAdapter(lan_Adapter);
        lan_list_1.setOnItemSelectedListener(this);
        lan_list_2.setAdapter(lan_Adapter);
        lan_list_2.setOnItemSelectedListener(this);

        input = findViewById(R.id.input);
        output = findViewById(R.id.output);
        translateButton = findViewById(R.id.translate_button);



        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TranslateClick(v);
            }
        });



    }

    public void TranslateClick(View v) {
        inputText = input.getText().toString();
        language_from = lan_list_1.getSelectedItem().toString();

        switch (language_from) {
            case "Afrikaans":
                language_from_code = "af";
                break;
            case "Arabic":
                language_from_code= "ar";
                break;
            case "Bangla":
                language_from_code = "bn";
                break;
            case "Bosnian (Latin)":
                language_from_code = "bs";
                break;
            case "Bulgarian":
                language_from_code = "bg";
                break;
            case "Cantonese (Traditional)":
                language_from_code = "yue";
                break;
            case "Catalan":
                language_from_code = "ca";
                break;
            case "Chinese Simplified":
                language_from_code = "zh-Hans";
                break;
            case "Chinese Traditional":
                language_from_code = "zh-Hant";
                break;
            case "Croatian":
                language_from_code = "hr";
                break;
            case "Czech":
                language_from_code = "cs";
                break;
            case "Danish":
                language_from_code = "da";
                break;
            case "Dutch":
                language_from_code = "nl";
                break;
            case "English":
                language_from_code = "en";
                break;
            case "Estonian":
                language_from_code = "et";
                break;
            case "Fijian":
                language_from_code = "fj";
                break;
            case "Filipino":
                language_from_code = "fil";
                break;
            case "Finnish":
                language_from_code = "fi";
                break;
            case "French":
                language_from_code = "fr";
                break;
            case "German":
                language_from_code = "de";
                break;
            case "Greek":
                language_from_code = "el";
                break;
            case "Haitian Creole":
                language_from_code = "ht";
                break;
            case "Hebrew":
                language_from_code = "he";
                break;
            case "Hindi":
                language_from_code = "hi";
                break;
            case "Hmong Daw":
                language_from_code = "mww";
                break;
            case "Hungarian":
                language_from_code = "hu";
                break;
            case "Icelandic":
                language_from_code = "is";
                break;
            case "Indonesian":
                language_from_code = "id";
                break;
            case "Irish":
                language_from_code = "ga";
                break;
            case "Italian":
                language_from_code = "it";
                break;
            case "Japanese":
                language_from_code = "ja";
                break;
            case "Kannada":
                language_from_code = "kn";
                break;
            case "Kiswahili":
                language_from_code = "sw";
                break;
            case "Klingon":
                language_from_code = "tlh";
                break;
            case "Klingon (plqaD)":
                language_from_code = "tlh-Qaak";
                break;
            case "Korean":
                language_from_code = "ko";
                break;
            case "Latvian":
                language_from_code = "lv";
                break;
            case "Lithuanian":
                language_from_code = "lt";
                break;
            case "Malagasy":
                language_from_code = "mg";
                break;
            case "Malay":
                language_from_code = "ms";
                break;
            case "Malayalam":
                language_from_code = "ml";
                break;
            case "Maltese":
                language_from_code = "mt";
                break;
            case "Maori":
                language_from_code = "mi";
                break;
            case "Norwegian":
                language_from_code = "nb";
                break;
            case "Persian":
                language_from_code = "fa";
                break;
            case "Polish":
                language_from_code = "pl";
                break;
            case "Portuguese (Brazil)":
                language_from_code = "pt-br";
                break;
            case "Portuguese (Portugal)":
                language_from_code = "pt-pt";
                break;
            case "Punjabi":
                language_from_code = "pa";
                break;
            case "Queretaro Otomi":
                language_from_code = "otq";
                break;
            case "Romanian":
                language_from_code = "ro";
                break;
            case "Russian":
                language_from_code = "ru";
                break;
            case "Samoan":
                language_from_code = "sm";
                break;
            case "Serbian (Cyrillic)":
                language_from_code = "sr-Cyrl";
                break;
            case "Serbian (Latin)":
                language_from_code = "sr-Latn";
                break;
            case "Slovak":
                language_from_code = "sk";
                break;
            case "Slovenian":
                language_from_code = "sl";
                break;
            case "Spanish":
                language_from_code = "es";
                break;
            case "Swedish":
                language_from_code = "sv";
                break;
            case "Tahitian":
                language_from_code = "ty";
                break;
            case "Tamil":
                language_from_code = "ta";
                break;
            case "Telugu":
                language_from_code = "te";
                break;
            case "Thai":
                language_from_code = "th";
                break;
            case "Tongan":
                language_from_code = "to";
                break;
            case "Turkish":
                language_from_code = "tr";
                break;
            case "Ukrainian":
                language_from_code = "uk";
                break;
            case "Urdu":
                language_from_code = "ur";
                break;
            case "Vietnamese":
                language_from_code = "vi";
                break;
            case "Welsh":
                language_from_code = "cy";
                break;
            case "Yucatec Maya":
                language_from_code = "yua";
                break;
            default:
                language_from_code = "undefined";
        }


        language_to = lan_list_2.getSelectedItem().toString();

        switch (language_to) {
            case "Afrikaans":
                language_to_code = "af";
                break;
            case "Arabic":
                language_to_code = "ar";
                break;
            case "Bangla":
                language_to_code = "bn";
                break;
            case "Bosnian (Latin)":
                language_to_code = "bs";
                break;
            case "Bulgarian":
                language_to_code = "bg";
                break;
            case "Cantonese (Traditional)":
                language_to_code = "yue";
                break;
            case "Catalan":
                language_to_code = "ca";
                break;
            case "Chinese Simplified":
                language_to_code = "zh-Hans";
                break;
            case "Chinese Traditional":
                language_to_code = "zh-Hant";
                break;
            case "Croatian":
                language_to_code = "hr";
                break;
            case "Czech":
                language_to_code = "cs";
                break;
            case "Danish":
                language_to_code = "da";
                break;
            case "Dutch":
                language_to_code = "nl";
                break;
            case "English":
                language_to_code = "en";
                break;
            case "Estonian":
                language_to_code = "et";
                break;
            case "Fijian":
                language_to_code = "fj";
                break;
            case "Filipino":
                language_to_code = "fil";
                break;
            case "Finnish":
                language_to_code = "fi";
                break;
            case "French":
                language_to_code = "fr";
                break;
            case "German":
                language_to_code = "de";
                break;
            case "Greek":
                language_to_code = "el";
                break;
            case "Haitian Creole":
                language_to_code = "ht";
                break;
            case "Hebrew":
                language_to_code = "he";
                break;
            case "Hindi":
                language_to_code = "hi";
                break;
            case "Hmong Daw":
                language_to_code = "mww";
                break;
            case "Hungarian":
                language_to_code = "hu";
                break;
            case "Icelandic":
                language_to_code = "is";
                break;
            case "Indonesian":
                language_to_code = "id";
                break;
            case "Irish":
                language_to_code = "ga";
                break;
            case "Italian":
                language_to_code = "it";
                break;
            case "Japanese":
                language_to_code = "ja";
                break;
            case "Kannada":
                language_to_code = "kn";
                break;
            case "Kiswahili":
                language_to_code = "sw";
                break;
            case "Klingon":
                language_to_code = "tlh";
                break;
            case "Klingon (plqaD)":
                language_to_code = "tlh-Qaak";
                break;
            case "Korean":
                language_to_code = "ko";
                break;
            case "Latvian":
                language_to_code = "lv";
                break;
            case "Lithuanian":
                language_to_code = "lt";
                break;
            case "Malagasy":
                language_to_code = "mg";
                break;
            case "Malay":
                language_to_code = "ms";
                break;
            case "Malayalam":
                language_to_code = "ml";
                break;
            case "Maltese":
                language_to_code = "mt";
                break;
            case "Maori":
                language_to_code = "mi";
                break;
            case "Norwegian":
                language_to_code = "nb";
                break;
            case "Persian":
                language_to_code = "fa";
                break;
            case "Polish":
                language_to_code = "pl";
                break;
            case "Portuguese (Brazil)":
                language_to_code = "pt-br";
                break;
            case "Portuguese (Portugal)":
                language_to_code = "pt-pt";
                break;
            case "Punjabi":
                language_to_code = "pa";
                break;
            case "Queretaro Otomi":
                language_to_code = "otq";
                break;
            case "Romanian":
                language_to_code = "ro";
                break;
            case "Russian":
                language_to_code = "ru";
                break;
            case "Samoan":
                language_to_code = "sm";
                break;
            case "Serbian (Cyrillic)":
                language_to_code = "sr-Cyrl";
                break;
            case "Serbian (Latin)":
                language_to_code = "sr-Latn";
                break;
            case "Slovak":
                language_to_code = "sk";
                break;
            case "Slovenian":
                language_to_code = "sl";
                break;
            case "Spanish":
                language_to_code = "es";
                break;
            case "Swedish":
                language_to_code = "sv";
                break;
            case "Tahitian":
                language_to_code = "ty";
                break;
            case "Tamil":
                language_to_code = "ta";
                break;
            case "Telugu":
                language_to_code = "te";
                break;
            case "Thai":
                language_to_code = "th";
                break;
            case "Tongan":
                language_to_code = "to";
                break;
            case "Turkish":
                language_to_code = "tr";
                break;
            case "Ukrainian":
                language_to_code = "uk";
                break;
            case "Urdu":
                language_to_code = "ur";
                break;
            case "Vietnamese":
                language_to_code = "vi";
                break;
            case "Welsh":
                language_to_code = "cy";
                break;
            case "Yucatec Maya":
                language_to_code = "yua";
                break;
            default:
                language_to_code = "undefined";
        }

        Log.w ("1", "got the language to and from");
        Future<String> result = es.submit(new SendRunTranslate(inputText,language_from_code,language_to_code));

        try {
            translationString = result.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.w("3", "result text is: \""+translationString+"\" ");

            output.setText(translationString);


    }


    /**
     * Additional thread to handle network operation
     */
    private class SendRunTranslate implements Callable<String> {
        String message, language_to, language_from;
        public SendRunTranslate(String message, String language_to, String language_from){
            this.message = message;
            this.language_to = language_to;
            this.language_from = language_from;
        }

        @Override
        public String call() throws Exception {
            String jsonToSend = "{\"message\": \""+inputText+"\", \"language_to\": \""+ language_to_code +"\", \"language_from\": \""+ language_from_code +"\"}";

            Log.w ("2", jsonToSend);
            Log.w("2.1", "input text: \""+inputText+"\" ");
            Log.w("2.2", "language from: \""+language_from_code+"\" ");
            Log.w("2.3","language to: \""+language_to_code+"\" ");

            try {
                translation = publicPostGetString("https://linguabox.azurewebsites.net/translate", jsonToSend);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return translation;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
