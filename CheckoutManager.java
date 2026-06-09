import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CheckoutManager implements WarnaTerminal {
    public void tampilkanRincianPesanan(List<OrderItem> pesananUser, boolean isFinalReceipt, Member member) {
        String formatKotak = "%-82s";

        if (!isFinalReceipt) {
            System.out.println();
            String judul = "                      --- RINCIAN PESANAN SEMENTARA ---";
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak, judul) + RESET + "\n");
        }

        List<OrderItem> listMakanan = new ArrayList<>();
        List<OrderItem> listMinuman = new ArrayList<>();

        for (OrderItem item : pesananUser) {
            if (item.getMenu() instanceof Makanan) listMakanan.add(item);
            else listMinuman.add(item);
        }

        cetakKategoriRincian("MAKANAN", listMakanan, isFinalReceipt, member);
        cetakKategoriRincian("MINUMAN", listMinuman, isFinalReceipt, member);

        if (!isFinalReceipt) {
            double grandSubtotal = 0;
            for (OrderItem item : pesananUser) {
                grandSubtotal += item.getSubtotal(member);
            }
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak, "") + RESET + "\n");
            String strGrand = String.format("   GRAND SUBTOTAL (Makanan + Minuman) : %.2f", grandSubtotal);
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak, strGrand) + RESET + "\n");
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak, "----------------------------------------------------------------------------------") + RESET + "\n");
        }
    }

    private void cetakKategoriRincian(String judul, List<OrderItem> list, boolean isFinalReceipt, Member member) {
        if (list.isEmpty()) return;

        String formatKotak = "%-82s";
        String bgList = isFinalReceipt ? BG_WHITE : BG_MGM_YELLOW;

        String katHeader = "KATEGORI: " + judul;
        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak, katHeader) + RESET + "\n");

        String colHeader = String.format("%-35s | %-12s | %-12s | %-12s", "[Nama+Kode+Qty+Harga/pcs]", "Total Harga", "Total Pajak", "Subtotal");
        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak, colHeader) + RESET + "\n");

        String garis = "----------------------------------------------------------------------------------";
        System.out.printf(bgList + BLACK_TEXT + String.format(formatKotak, garis) + RESET + "\n");

        double sumHarga = 0, sumPajak = 0, sumSubtotal = 0;

        for (OrderItem item : list) {
            Menu m = item.getMenu();
            String col1 = String.format("%s (%s) x%d @%.0f", m.getNama(), m.getKode(), item.getQty(), m.getHarga());
            double col2 = item.getHargaNormalTotal();
            double col3 = item.getPajakTotal(member);
            double col4 = item.getSubtotal(member);

            sumHarga += col2;
            sumPajak += col3;
            sumSubtotal += col4;

            String rowData = String.format("%-35s | %-12.2f | %-12.2f | %-12.2f", col1, col2, col3, col4);
            System.out.printf(bgList + BLACK_TEXT + String.format(formatKotak, rowData) + RESET + "\n");
        }

        System.out.printf(bgList + BLACK_TEXT + String.format(formatKotak, garis) + RESET + "\n");
        String totalRow = String.format("%-35s | %-12.2f | %-12.2f | %-12.2f", "TOTAL " + judul, sumHarga, sumPajak, sumSubtotal);
        System.out.printf(bgList + BLACK_TEXT + String.format(formatKotak, totalRow) + RESET + "\n");
    }

    private PaymentChannel pilihChannelPembayaran(Scanner sc) {
        System.out.println("\n--- Pilih Channel Pembayaran ---");
        System.out.println("1. Tunai (Diskon 0%)");
        System.out.println("2. QRIS (Diskon 5%)");
        System.out.println("3. eMoney (Diskon 7%, Admin 20 IDR)");

        while (true) {
            System.out.print("Masukkan pilihan (1-3, 'CC' batal): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("CC")) System.exit(0);

            try {
                int pilihan = Integer.parseInt(input);
                switch (pilihan) {
                    case 1: return new Tunai();
                    case 2: return new QRIS();
                    case 3: return new EMoney();
                    default: System.out.println(RED + "Pilihan tidak valid." + RESET);
                }
            } catch (Exception e) {
                System.out.println(RED + "Error: Input harus angka!" + RESET);
            }
        }
    }

    private CurrencyConverter pilihMataUang(Scanner sc) {
        System.out.println("\n--- Pilih Mata Uang Pembayaran ---");
        System.out.println("1. USD (1 USD = 15 IDR)");
        System.out.println("2. JPY (10 JPY = 1 IDR)");
        System.out.println("3. MYR (1 MYR = 4 IDR)");
        System.out.println("4. EUR (1 EUR = 14 IDR)");
        System.out.println("5. IDR (1 IDR = 1 IDR)");

        while (true) {
            System.out.print("Masukkan pilihan (1-5, 'CC' batal): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("CC")) System.exit(0);

            try {
                int pilihan = Integer.parseInt(input);
                switch (pilihan) {
                    case 1: return new USD();
                    case 2: return new JPY();
                    case 3: return new MYR();
                    case 4: return new EUR();
                    case 5: return new IDR();
                    default: System.out.println(RED + "Pilihan tidak valid." + RESET);
                }
            } catch (Exception e) {
                System.out.println(RED + "Error: Input harus angka!" + RESET);
            }
        }
    }

    public void prosesTransaksiFinal(Scanner sc, List<OrderItem> pesananUser, Member member) {
        
        int poinAwal = (member != null) ? member.getPoin() : 0;
        
        double totalSebelumPajakDiskonIDR = 0, totalPajakIDR = 0;

        for (OrderItem item : pesananUser) {
            totalSebelumPajakDiskonIDR += item.getHargaNormalTotal();
            totalPajakIDR += item.getPajakTotal(member);
        }
        double subtotalDenganPajakIDR = totalSebelumPajakDiskonIDR + totalPajakIDR;

        PaymentChannel pembayaran = pilihChannelPembayaran(sc);
        
        double diskon = pembayaran.hitungDiskon(subtotalDenganPajakIDR);
        double admin = pembayaran.getAdminFee();
        double totalSetelahPajakDiskonIDR = subtotalDenganPajakIDR - diskon + admin;

        System.out.println(CYAN + "\n--- PROSES PERHITUNGAN CHANNEL PEMBAYARAN ---" + RESET);
        System.out.printf("Subtotal Awal (Harga + Pajak) : IDR %.2f\n", subtotalDenganPajakIDR);
        System.out.printf("Channel yang Dipilih          : %s\n", pembayaran.getNamaChannel());
        System.out.printf("Kalkulasi Diskon              : - IDR %.2f\n", diskon);
        System.out.printf("Kalkulasi Biaya Admin         : + IDR %.2f\n", admin);
        System.out.printf("Total Setelah Channel         : IDR %.2f\n", totalSetelahPajakDiskonIDR);

        
        CurrencyConverter mataUang = pilihMataUang(sc);

        double poinDigunakan = 0;
        double idrDariPoin = 0;

        if (member != null && mataUang instanceof IDR) {
            int poinYangBisaDipakai = member.getPoin();
            double maxPotonganIDR = poinYangBisaDipakai * 2.0;
            
            if (maxPotonganIDR > 0) {
                if (maxPotonganIDR >= totalSetelahPajakDiskonIDR) {
                    poinDigunakan = Math.ceil(totalSetelahPajakDiskonIDR / 2.0);
                    idrDariPoin = totalSetelahPajakDiskonIDR;
                    totalSetelahPajakDiskonIDR = 0;
                } else {
                    poinDigunakan = poinYangBisaDipakai;
                    idrDariPoin = maxPotonganIDR;
                    totalSetelahPajakDiskonIDR -= idrDariPoin;
                }
                member.kurangiPoin((int) poinDigunakan);
                System.out.println(GREEN + "\n[INFO POIN] Menggunakan " + (int)poinDigunakan + " poin untuk potongan senilai IDR " + idrDariPoin + RESET);
                System.out.printf(CYAN + "Total Tagihan Setelah Poin    : IDR %.2f\n" + RESET, totalSetelahPajakDiskonIDR);
            }
        }

        double tagihanValas = mataUang.convertFromIdr(totalSetelahPajakDiskonIDR);
        String simbol = mataUang.getSymbol();

        System.out.println(CYAN + "\n--- PROSES PERHITUNGAN MATA UANG ---" + RESET);
        System.out.printf("Total Tagihan IDR Akhir       : IDR %.2f\n", totalSetelahPajakDiskonIDR);
        System.out.printf("Mata Uang Tujuan              : %s\n", simbol);
        if (mataUang instanceof USD) {
            System.out.printf("Rumus Konversi                : %.2f / 15.0\n", totalSetelahPajakDiskonIDR);
        } else if (mataUang instanceof JPY) {
            System.out.printf("Rumus Konversi                : %.2f * 10.0\n", totalSetelahPajakDiskonIDR);
        } else if (mataUang instanceof MYR) {
            System.out.printf("Rumus Konversi                : %.2f / 4.0\n", totalSetelahPajakDiskonIDR);
        } else if (mataUang instanceof EUR) {
            System.out.printf("Rumus Konversi                : %.2f / 14.0\n", totalSetelahPajakDiskonIDR);
        } else if (mataUang instanceof IDR) {
            System.out.printf("Rumus Konversi                : %.2f * 1.0\n", totalSetelahPajakDiskonIDR);
        }
        System.out.printf("Hasil Konversi Final          : %.2f %s\n", tagihanValas, simbol);

        System.out.println("\n=========================================");
        System.out.printf("TOTAL TAGIHAN SEMENTARA: %.2f %s\n", tagihanValas, simbol);
        System.out.println("=========================================");

        
        int attempts = 0;
        double bayar = 0;
        boolean success = false;

        if (tagihanValas <= 0) {
            success = true;
            bayar = 0;
            System.out.println(GREEN + "Tagihan telah lunas oleh Poin Member!" + RESET);
        } else {
            while (attempts < 3) {
                System.out.printf("Masukkan nominal pembayaran (%s) atau 'CC' untuk batal: ", simbol);
                String inputBayar = sc.nextLine().trim();
                if (inputBayar.equalsIgnoreCase("CC")) System.exit(0);

                try {
                    bayar = Double.parseDouble(inputBayar);
                    if (bayar < tagihanValas) {
                        System.out.println(RED + "Nominal kurang!" + RESET);
                        attempts++;
                    } else {
                        success = true;
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(RED + "Error: Input harus angka!" + RESET);
                    attempts++;
                }
            }
        }

        if (!success) {
            System.out.println("\nPesanan Dibatalkan karena gagal bayar 3 kali.");
            System.exit(0);
        }

        
        int poinDapat = 0;
        if (member != null) {
            int totalQty = 0;
            for(OrderItem item : pesananUser) totalQty += item.getQty();
            poinDapat = totalQty / 10;
            
            if (poinDapat > 0) {
                if (member.getKode().toUpperCase().contains("A")) {
                    poinDapat *= 2; 
                    System.out.println(YELLOW + "\n[BONUS MEMBERSHIP] Kode member Anda mengandung huruf 'A'. Poin yang diperoleh DIGANDAKAN!" + RESET);
                }
                member.tambahPoin(poinDapat);
                System.out.println(YELLOW + "Selamat! Anda mendapatkan " + poinDapat + " poin baru dari transaksi ini!" + RESET);
            }
        }

        
        String formatKotak82 = "%-82s";
        String garisBatas = "==================================================================================";

        System.out.println();
        System.out.printf(BG_WHITE + GREEN + String.format(formatKotak82, garisBatas) + RESET + "\n");
        String headerSukses = "                               PEMBAYARAN SUKSES";
        System.out.printf(BG_WHITE + GREEN + String.format(formatKotak82, headerSukses) + RESET + "\n");
        System.out.printf(BG_WHITE + GREEN + String.format(formatKotak82, garisBatas) + RESET + "\n");

        tampilkanRincianPesanan(pesananUser, true, member);

        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "") + RESET + "\n");
        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "--- RINGKASAN TRANSAKSI ---") + RESET + "\n");

        String strTotalSblm = String.format("Total SEBELUM pajak & diskon (IDR) : %.2f", totalSebelumPajakDiskonIDR);
        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, strTotalSblm) + RESET + "\n");

        double totalSblmPoin = totalSebelumPajakDiskonIDR + totalPajakIDR - diskon + admin;
        String strTotalStlh = String.format("Total SETELAH pajak & diskon (IDR) : %.2f", totalSblmPoin);
        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, strTotalStlh) + RESET + "\n");

        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "") + RESET + "\n");
        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "Metode Pembayaran: " + pembayaran.getNamaChannel()) + RESET + "\n");

        String strDiskon = String.format("- Diskon : IDR %.2f", diskon);
        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, strDiskon) + RESET + "\n");

        String strAdmin = String.format("- Admin  : IDR %.2f", admin);
        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, strAdmin) + RESET + "\n");
        
        if (idrDariPoin > 0) {
            String strPoin = String.format("- Potongan Poin Member : IDR %.2f", idrDariPoin);
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, strPoin) + RESET + "\n");
        }

        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "") + RESET + "\n");
        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "--- KONVERSI MATA UANG (" + simbol + ") ---") + RESET + "\n");

        String strKonvStlh = String.format("Total Akhir Tagihan : %.2f %s", tagihanValas, simbol);
        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, strKonvStlh) + RESET + "\n");

        if (bayar > tagihanValas) {
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "") + RESET + "\n");
            String strKembali = String.format("NOMINAL KEMBALIAN (%s): %.2f", simbol, (bayar - tagihanValas));
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, strKembali) + RESET + "\n");
        }

        
        if (member != null) {
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "") + RESET + "\n");
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "--- INFORMASI MEMBERSHIP ---") + RESET + "\n");
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "Nama Member   : " + member.getNama() + " (" + member.getKode() + ")") + RESET + "\n");
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "Poin Awal     : " + poinAwal) + RESET + "\n");
            if (poinDigunakan > 0) {
                System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "Poin Terpakai : -" + (int)poinDigunakan) + RESET + "\n");
            }
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "Poin Didapat  : +" + poinDapat) + RESET + "\n");
            System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "Poin Akhir    : " + member.getPoin()) + RESET + "\n");
        }

        System.out.printf(BG_WHITE + BLACK_TEXT + String.format(formatKotak82, "") + RESET + "\n");
        String thanks = "TERIMA KASIH DAN SILAHKAN DATANG KEMBALI";
        System.out.printf(BG_WHITE + GREEN + String.format(formatKotak82, thanks) + RESET + "\n");
        System.out.printf(BG_WHITE + GREEN + String.format(formatKotak82, garisBatas) + RESET + "\n");
    }
}