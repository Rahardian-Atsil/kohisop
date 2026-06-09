import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MembershipManager {
    private List<Member> daftarMember;

    public MembershipManager() {
        this.daftarMember = new LinkedList<>();
    }

    public Member registerMember(String nama) {
        String kode = generateKode();
        Member newMember = new Member(kode, nama, 0);
        daftarMember.add(newMember);
        return newMember;
    }

    public Member findMember(String kode) {
        for (Member m : daftarMember) {
            if (m.getKode().equalsIgnoreCase(kode)) {
                return m;
            }
        }
        return null;
    }

    private String generateKode() {
        String chars = "ABCDEF0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}