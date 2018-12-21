package de.iteratec.iteraOfficeMap.utility;

import java.util.regex.Pattern;

public class MacAddressValidator {

    private static Pattern patternMacPairs = Pattern.compile("^([a-fA-F0-9]{2}[:,.-]?){5}[a-fA-F0-9]{2}$");

    public static boolean isValidMac(String macAddress) {
        if (macAddress == null) {
            return false;
        }
        return patternMacPairs.matcher(macAddress).find();
    }

    public static String convertMac(String macAddress) {
        if (macAddress == null || macAddress.equals("")) {
            return null;
        }
        if (isValidMac(macAddress)) {
            macAddress = macAddress.replaceAll("\\W", ":");
            macAddress = macAddress.toUpperCase();
            return macAddress;
        } else throw new IllegalArgumentException("Invalid MAC Address");
    }

}
