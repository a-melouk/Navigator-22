package com.esi.navigator_22;

import java.text.DecimalFormat;

public class Routing {





    String format(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }


}
