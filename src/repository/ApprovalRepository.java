package repository;

import model.ApprovalLog;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ApprovalRepository {
    private final String path;

    public ApprovalRepository(String path) {
        this.path = path;
    }

    public List<ApprovalLog> layTatCa() throws IOException {
        List<ApprovalLog> ds = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) return ds;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                ApprovalLog log = ApprovalLog.fromLine(line);
                if (log != null) ds.add(log);
            }
        }
        return ds;
    }

    public void them(ApprovalLog log) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            bw.write(log.toLine());
            bw.newLine();
        }
    }
}
