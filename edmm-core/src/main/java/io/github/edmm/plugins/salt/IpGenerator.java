package io.github.edmm.plugins.salt;

import java.util.LinkedList;
import java.util.regex.Pattern;

public class IpGenerator {
    // Used ips
    private LinkedList<Integer> usedIPs;
    // Lowerbound 10.0.0.0
    private int lowerBound = 0b00001010_00000000_00000000_00000000;
    // Upperbound 10.0.0.255
    private int upperBound = 0b00001010_00000000_00000000_11111111;
    private int index;

    /**
     * Costructor
     */
    public IpGenerator() {
        this.index = lowerBound;
        this.usedIPs = new LinkedList<>();
    }

    IpGenerator(String lower, String upper) throws IllegalArgumentException {
        if (stringToIp(lower) >= stringToIp(upper) ||
            stringToIp(lower) < this.lowerBound || stringToIp(upper) > this.upperBound) {
            throw new IllegalArgumentException();
        }
        this.lowerBound = stringToIp(lower);
        this.upperBound = stringToIp(upper);
        this.index = lowerBound;
        this.usedIPs = new LinkedList<>();
    }

    /**
     * Convert dotted decimal ip to binary format
     *
     * @param address Dotted decimal ip
     * @return Binary ip
     */
    private static int stringToIp(String address) {
        int result = 0;
        // Intera sugli ottetti
        for (String part : address.split(Pattern.quote("."))) {
            result = result << 8;
            result |= Integer.parseInt(part);
        }
        return result;
    }

    /**
     * Return next available ip
     */
    String getNextIp() throws AllUsedIpsException {
        int arraySize = upperBound - lowerBound;
        if (usedIPs.size() == arraySize) throw new AllUsedIpsException();
        usedIPs.add(index++);
        if (index == upperBound) index = lowerBound;
        return String.format("%d.%d.%d.%d", (index >> 24 & 0xff), (index >> 16 & 0xff),
            (index >> 8 & 0xff), (index & 0xff));
    }
}

class AllUsedIpsException extends Exception {
}
