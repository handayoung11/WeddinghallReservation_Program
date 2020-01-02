package 대전;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Setting {
	static Statement stmt;
	static Connection con;
	static final String path="./지급자료/";
	
	static {
		try {
			con=DriverManager.getConnection("jdbc:mysql://localhost?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true&allowLoadLocalInfile=true", "root", "1234");
			stmt=con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Setting() {
		cDB();
	}
	
	void cDB() {
		execute("drop database if exists wedding");
		execute("create database wedding default character set utf8");
		execute("use wedding");
		execute("set global local_infile=1");
		table("weddinghall", "weddinghall_index int primary key not null auto_increment, weddinghall_name varchar(20), weddinghall_address varchar(50), weddinghall_accommodate int, weddinghall_fee int");
		table("weddingtype", "weddingtype_index int primary key not null auto_increment, weddingtype_name varchar(15)");
		table("mealtype", "mealtype_index int primary key not null auto_increment, mealtype_name varchar(5), mealtype_price int");
		table("reservation", "reservation_code int primary key not null, weddinghall_index int, reservation_personnel int, weddingtype_index int, mealtype_index int, album int, letter int, dress int, date date, pay int, foreign key(weddinghall_index) references weddinghall(weddinghall_index) on delete cascade on update cascade, foreign key(weddingtype_index) references weddingtype(weddingtype_index) on delete cascade on update cascade, foreign key(mealtype_index) references mealtype(mealtype_index) on delete cascade on update cascade");
		table("weddinghall_weddingtype", "weddinghall_index int, weddingtype_index int, primary key(weddinghall_index,weddingtype_index), foreign key(weddinghall_index) references weddinghall(weddinghall_index) on delete cascade on update cascade, foreign key(weddingtype_index) references weddingtype(weddingtype_index) on delete cascade on update cascade");
		table("weddinghall_mealtype", "weddinghall_index int, mealtype_index int, primary key(weddinghall_index,mealtype_index), foreign key(weddinghall_index) references weddinghall(weddinghall_index) on delete cascade on update cascade, foreign key(mealtype_index) references mealtype(mealtype_index) on delete cascade on update cascade");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, delete on wedding.* to user@localhost");
	}
	
	void table(String t, String c) {
		execute("create table "+t+"("+c+")");
		execute("load data local infile '"+path+t+".txt' into table "+t+" ignore 1 lines");
	}
	
	static void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Setting();
	}
}
