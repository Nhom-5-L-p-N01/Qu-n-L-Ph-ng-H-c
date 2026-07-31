import java.io.*;
import java.util.*;

public class AccountRepository {
    private final String path;

    public AccountRepository() {
        this("data_accounts.txt");
    }

    public AccountRepository(String path) {
        this.path = path;
    }

    public List<Account> layTatCa() {
        List<Account> ds = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) return ds;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Account acc = Account.fromLine(line);
                if (acc != null) ds.add(acc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ds;
    }

    private void luu(List<Account> ds) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (Account acc : ds) {
                bw.write(acc.toLine());
                bw.newLine();
            }
        }
    }

    public boolean emailDaTonTai(String email) {
        for (Account acc : layTatCa()) {
            if (acc.getEmail().equalsIgnoreCase(email)) return true;
        }
        return false;
    }

    public boolean them(Account acc) {
        if (emailDaTonTai(acc.getEmail())) return false;
        List<Account> ds = layTatCa();
        ds.add(acc);
        try {
            luu(ds);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Account dangNhap(String email, String matKhau) {
        for (Account acc : layTatCa()) {
            if (acc.getEmail().equalsIgnoreCase(email) && acc.getMatKhau().equals(matKhau)) {
                return acc;
            }
        }
        return null;
    }
}
