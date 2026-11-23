/**
 * Nothing OS & CMF Phone Widget Limit Unlocker
 * Injects custom AI/Card widget limits into Nothing OS's OnlineConfig ContentProvider.
 *
 * Author: @musabtvt
 * License: MIT
 */
import java.lang.reflect.Method;

public class EncryptAndInsert {
    public static void main(String[] args) {
        try {
            int widgetLimit = 7;
            int version = 2000000;

            if (args.length > 0) {
                try {
                    widgetLimit = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid widget limit provided: " + args[0] + ", using default (7)");
                }
            }
            if (args.length > 1) {
                try {
                    version = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid version provided: " + args[1] + ", using default (2000000)");
                }
            }

            System.out.println("==================================================");
            System.out.println("Nothing OS Widget Limit Unlocker (@musabtvt)");
            System.out.println("Target Widget Limit : " + widgetLimit);
            System.out.println("Target Config Version: " + version);
            System.out.println("==================================================");

            // Construct exact SingleResultBean JSON expected by Nothing Launcher
            String json = "[{\"version\":" + version + ",\"aiWidgetConfig\":{\"maxAiWidgetNumber\":" + widgetLimit + "}}]";

            // Resolve Android reflection classes
            Class<?> base64Class = Class.forName("android.util.Base64");
            Method b64Encode = base64Class.getMethod("encode", byte[].class, int.class);
            Method b64Decode = base64Class.getMethod("decode", byte[].class, int.class);
            Method b64EncodeToString = base64Class.getMethod("encodeToString", byte[].class, int.class);

            Class<?> utilsClass = Class.forName("com.nothing.utils.Utils");
            Method encryptMethod = utilsClass.getMethod("encrypt", byte[].class, String.class);
            Method decryptMethod = utilsClass.getMethod("decrypt", byte[].class, String.class);

            // Step 1: Base64 encode JSON
            byte[] step2 = (byte[]) b64Encode.invoke(null, json.getBytes("UTF-8"), 0);

            // Step 2: Encrypt with system key ("")
            byte[] step1 = (byte[]) encryptMethod.invoke(null, step2, "");

            // Step 3: Base64 encode to final string
            String configContent = (String) b64EncodeToString.invoke(null, step1, 0);
            configContent = configContent.trim();

            // Self-test verification roundtrip
            byte[] decStep1 = (byte[]) b64Decode.invoke(null, configContent.getBytes("UTF-8"), 0);
            byte[] decStep2 = (byte[]) decryptMethod.invoke(null, decStep1, "");
            byte[] decStep3 = (byte[]) b64Decode.invoke(null, decStep2, 0);
            String decrypted = new String(decStep3, "UTF-8");

            if (!json.equals(decrypted)) {
                System.err.println("[-] FATAL: Decryption verification mismatch!");
                System.exit(1);
            }
            System.out.println("[+] Encryption verification succeeded.");

            // Clear previous entry
            Process pDel = Runtime.getRuntime().exec(new String[]{
                "content", "delete", "--uri", "content://com.nothing.proxy.onlineconfig.provider/launcher_single_config"
            });
            pDel.waitFor();

            // Insert new configuration into OnlineConfig provider
            Process pIns = Runtime.getRuntime().exec(new String[]{
                "content", "insert",
                "--uri", "content://com.nothing.proxy.onlineconfig.provider/launcher_single_config",
                "--bind", "project_id:s:launcher_single_config",
                "--bind", "config_content:s:" + configContent
            });
            int exitCode = pIns.waitFor();

            if (exitCode == 0) {
                System.out.println("[+] Successfully injected config into OnlineConfig Provider!");
                System.out.println("[+] Nothing Launcher will dynamically reload and expand AI widget limit to " + widgetLimit + ".");
            } else {
                System.err.println("[-] Failed to insert into ContentProvider (exit code: " + exitCode + ")");
                System.exit(exitCode);
            }

        } catch (Throwable t) {
            System.err.println("[-] Execution failed: " + t.getMessage());
            t.printStackTrace();
            System.exit(1);
        }
    }
}
