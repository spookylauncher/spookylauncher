package io.github.spookylauncher.util.io;

import io.github.spookylauncher.advio.security.hash.HashCalculators;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Collections;

public final class Identifier {
    public static byte[] getMACHash() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            DataOutputStream out = new DataOutputStream(baos);

            for(NetworkInterface i : Collections.list(NetworkInterface.getNetworkInterfaces())) dump(i, out);

            out.close();

            return HashCalculators.get("MD5").calculate(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void dump(NetworkInterface i, DataOutput out) throws IOException {
        if(i == null) return;

        out.writeInt(i.getIndex());
        if(i.getHardwareAddress() != null) out.write(i.getHardwareAddress());
        out.writeInt(i.getMTU());
        out.writeUTF(i.getName());
        out.writeUTF(i.getDisplayName());

        byte f1 = 0b1, f2 = 0b10, f3 = 0b100, f4 = 0b1000;

        out.writeByte(
                (i.isUp() ? f1 : 0) | (i.isPointToPoint() ? f2 : 0) |
                  (i.isLoopback() ? f3 : 0) | (i.isVirtual() ? f4 : 0)
        );

        for(NetworkInterface sub : Collections.list(i.getSubInterfaces())) dump(sub, out);

        dump(i.getParent(), out);
    }
}