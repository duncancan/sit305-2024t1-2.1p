package com.example.w2;

// Default imports
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// My imports. I use HashMap to create a lookup table between unit names and their CM equivalents
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Create our lookup table ("LUT") of SI Unit equivalents for distance and weight units
        HashMap<String, Double> siLUT = new HashMap<String, Double>();
        siLUT.put("Inches",0.0254);
        siLUT.put("Feet",0.3048);
        siLUT.put("Yards",0.9144);
        siLUT.put("Miles",1609.344);
        siLUT.put("Centimetres",0.01);
        siLUT.put("Meters",1.0);
        siLUT.put("Kilometres",1000.0);
        siLUT.put("Pounds",0.45359237);
        siLUT.put("Ounces",0.028349523125);
        siLUT.put("Short tons",907.18474);
        siLUT.put("Long tons",1016.047);
        siLUT.put("Grams", 0.001);
        siLUT.put("Kilograms", 1.0);
        siLUT.put("Tonnes (metric)",1000.0);

        // Create a lookup table of unit types for each unit, in order to perform data validation
        HashMap<String, String> unitTypes = new HashMap<>();
        unitTypes.put("Inches","Length");
        unitTypes.put("Feet","Length");
        unitTypes.put("Yards","Length");
        unitTypes.put("Miles","Length");
        unitTypes.put("Centimetres","Length");
        unitTypes.put("Meters","Length");
        unitTypes.put("Kilometres","Length");
        unitTypes.put("Pounds","Mass");
        unitTypes.put("Ounces","Mass");
        unitTypes.put("Short tons","Mass");
        unitTypes.put("Long tons","Mass");
        unitTypes.put("Grams", "Mass");
        unitTypes.put("Kilograms", "Mass");
        unitTypes.put("Tonnes (metric)","Mass");
        unitTypes.put("Celsius", "Temperature");
        unitTypes.put("Fahrenheit", "Temperature");
        unitTypes.put("Kelvin", "Temperature");

        Button convertBtn = findViewById(R.id.convert_button);
        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Source and destination text fields and their values
                EditText inputValField = findViewById(R.id.value_input);
                TextView outputValField = findViewById(R.id.result_output);
                double inputValue = Double.parseDouble(inputValField.getText().toString());
                double outputValue;

                // Get source and destination units
                Spinner srcSpinner = findViewById(R.id.source_unit_spinner);
                String srcUnit = srcSpinner.getSelectedItem().toString();
                Spinner destSpinner = findViewById(R.id.destination_unit_spinner);
                String destUnit = destSpinner.getSelectedItem().toString();

                // Look up types of units; if they don't match, display an error message
                String srcUnitType = unitTypes.get(srcUnit);
                String destUnitType = unitTypes.get(destUnit);
                if (srcUnitType != destUnitType) {
                    outputValField.setText(String.format("Can't convert between %s and %s unit types.",
                            srcUnitType.toLowerCase(), destUnitType.toLowerCase()));
                    return;
                }

                // Convert masses and lengths from source to destination units
                if (srcUnitType == "Mass" || srcUnitType == "Length") {
                    double cmEquiv = inputValue * siLUT.get(srcUnit); // convert input to cm
                    outputValue = cmEquiv / siLUT.get(destUnit); // convert cm to output
                    outputValField.setText(String.format("%.4f", outputValue));
                    return;
                }

                // If they're temperature units, handle them a bit differently to masses and lengths
                else if (srcUnitType == "Temperature") {
                    // First, convert to Celsius
                    double celsEquiv;
                    switch (srcUnit) {
                        case "Fahrenheit":
                            celsEquiv = (inputValue - 32) / 1.8;
                            break;
                        case "Kelvin":
                            celsEquiv = inputValue - 273.15;
                            break;
                        default: // If it's celsius
                            celsEquiv = inputValue;
                    }
                    // Then convert to output unit
                    switch (destUnit) {
                        case "Fahrenheit":
                            outputValue = celsEquiv * 1.8 + 32;
                            break;
                        case "Kelvin":
                            outputValue = celsEquiv + 273.15;
                            break;
                        default: //  If it's celsius
                            outputValue = celsEquiv;
                    }

                    outputValField.setText(String.format("%.4f", outputValue));
                    return;
                }
            };
        });
    }
}