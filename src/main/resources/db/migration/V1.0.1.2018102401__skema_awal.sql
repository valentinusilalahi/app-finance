CREATE TABLE bank(
	id VARCHAR(36),
	kode VARCHAR(255) NOT NULL,
	nama VARCHAR(255) NOT NULL,
	nomor_rekening VARCHAR(255) NOT NULL,
	nama_rekening VARCHAR(255) NOT NULL,
	jumlah_digit_virtual_account INT NOT NULL DEFAULT 0,
	PRIMARY KEY (id),
	UNIQUE(kode)
);

CREATE TABLE jenis_tagihan (
  id VARCHAR(36),
  kode VARCHAR(10),
  nama VARCHAR(255) NOT NULL,
  tipe_pembayaran VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (kode),
  UNIQUE (nama)
);

CREATE TABLE debitur (
  id VARCHAR(36),
  nomor_debitur VARCHAR(255) NOT NULL,
  nama VARCHAR(255) NOT NULL,
  email VARCHAR(255),
  no_hp VARCHAR(255),
  PRIMARY KEY (id),
  UNIQUE (nomor_debitur)
);

CREATE TABLE tagihan (
  id VARCHAR(36),
  id_debitur VARCHAR(36) NOT NULL,
  id_jenis_tagihan VARCHAR(36) NOT NULL,
  nomor VARCHAR(255),
  nilai_tagihan DECIMAL(19, 2) NOT NULL,
  jumlah_pembayaran DECIMAL(19, 2) NOT NULL,
  tanggal_tagihan DATE NOT NULL,
  tanggal_jatuh_tempo DATE NOT NULL,
  keterangan VARCHAR(255),
  status_tagihan VARCHAR(255) NOT NULL,
  status_pembayaran VARCHAR(255) NOT NULL,
  status_notifikasi VARCHAR(255) NOT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id),
  UNIQUE (nomor),
  FOREIGN KEY (id_jenis_tagihan) REFERENCES jenis_tagihan (id),
  FOREIGN KEY (id_debitur) REFERENCES debitur (id)
);

CREATE TABLE virtual_account (
  id VARCHAR(36),
  id_bank VARCHAR(36) NOT NULL,
  id_tagihan VARCHAR(36) NOT NULL,
  nomor VARCHAR(255),
  va_status VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (id_bank) REFERENCES bank (id),
  FOREIGN KEY (id_tagihan) REFERENCES tagihan (id),
  UNIQUE (id_tagihan, id_bank)
);

CREATE TABLE jenis_tagihan_bank (
  id_jenis_tagihan VARCHAR(36),
  id_bank VARCHAR(36),
  PRIMARY KEY (id_jenis_tagihan, id_bank),
  FOREIGN KEY (id_bank) REFERENCES bank (id),
  FOREIGN KEY (id_jenis_tagihan) REFERENCES jenis_tagihan (id)
);

CREATE TABLE pembayaran (
  id VARCHAR(36),
  id_tagihan VARCHAR(36) NOT NULL,
  id_bank VARCHAR(36),
  id_virtual_account VARCHAR(36),
  waktu_transaksi TIMESTAMP NOT NULL,
  jumlah DECIMAL(19, 2) NOT NULL,
  jenis_pembayaran VARCHAR(255) NOT NULL,
  referensi VARCHAR(255) NOT NULL,
  keterangan VARCHAR(255),
  PRIMARY KEY (id),
  FOREIGN KEY (id_tagihan) REFERENCES tagihan (id),
  FOREIGN KEY (id_bank) REFERENCES bank (id),
  FOREIGN KEY (id_virtual_account) REFERENCES virtual_account (id)
);

CREATE TABLE bukti_pembayaran (
  id VARCHAR(36),
  id_pembayaran VARCHAR(36) NOT NULL,
  lokasi_file VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (id_pembayaran) REFERENCES pembayaran (id),
  UNIQUE (lokasi_file)
);

CREATE TABLE running_number (
  id VARCHAR(36),
  pemakaian VARCHAR(255) NOT NULL,
  prefix VARCHAR(255) NOT NULL,
  last_number BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE (prefix)
);