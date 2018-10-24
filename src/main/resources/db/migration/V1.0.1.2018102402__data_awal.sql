insert into bank (id, kode, nama, nomor_rekening, nama_rekening, jumlah_digit_virtual_account) values
  ('BNI001', '009', 'BNI Life','1234567890', 'Yayasan', 12);

insert into bank (id, kode, nama, nomor_rekening, nama_rekening, jumlah_digit_virtual_account) values
  ('BSM001', '451', 'BSM', '9876543210', 'STEI', 8);

insert into jenis_tagihan (id, kode, nama, tipe_pembayaran) values
  ('PMB2017', '01', 'Pendaftaran 2018', 'CLOSED');

insert into jenis_tagihan (id, kode, nama, tipe_pembayaran) values
  ('DU2017', '02', 'Daftar Ulang', 'INSTALLMENT');

insert into jenis_tagihan (id, kode, nama, tipe_pembayaran) values
  ('ASRAMA2018', '03', 'Asrama', 'CLOSED');

insert into jenis_tagihan (id, kode, nama, tipe_pembayaran) values
  ('sumbangan', '04', 'Sumbangan dan Bantuan', 'OPEN');