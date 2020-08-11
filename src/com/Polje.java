package com;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Polje extends JPanel implements ActionListener,KeyListener{

	private static final long serialVersionUID = 1;  // obavestava da li se ova klasa podudara sa objektom,
													// napravljenog u Main klasi !!!
	
	// velicine polja
	protected final int p_sirina = 520;
	protected final int p_visina = 400;
	
	// velicine i postavke zmije
	protected final int zglob = 16;   // 16px slika
	protected  int duzina_zmije = 3;
	protected  int pocPoz = 0;
	
	protected  int p_velicina = (p_sirina * p_visina) / (zglob * zglob); // ukupno broja zgloba zmije
	
	// pozicija na x i y osi svakog zgloba zmije
	protected int x[] = new int[p_velicina];
	protected int y[] = new int[p_velicina];
	
	// dozvole za kretanje zmije
	protected boolean levo = false;
	protected boolean desno = false;
	protected boolean gore = false;
	protected boolean dole = false;
	
	
	// slike naslova i zmije
	protected ImageIcon naslov;
	protected ImageIcon glavaDesno;
	protected ImageIcon glavaDole;
	protected ImageIcon glavaLevo;
	protected ImageIcon glavaGore;
	protected ImageIcon telo;
	protected ImageIcon jabuka;
	
	// vreme kretanja i kasnjenja svakog zgloba
	protected Timer vreme;
	protected int kasnjenje = 180;   // pocetna brzina
	
	// podesavanje jabuke
	protected int x_jab[] = {33,49,65,81,97,113,129,145,161,177,193,209,225,241,257,273,289,305,321,337,353,369,385,401,417,433,449,465,481,497,513,529};
	protected int y_jab[] = {140,156,172,188,204,220,236,252,268,284,300,316,332,348,364,380,396,412,428,444,460,476,492,508,524};
	
	Random random = new Random();
	
	protected int xpoz = random.nextInt(32);  // vrednosti po x osi
	protected int ypoz = random.nextInt(23);  // vrednosti po y osi
	
	// rezultat
	protected int rezultat = 0;
	protected String najbolji_rezultat = "niko:0";
	
	
	public Polje(){
		addKeyListener(this);  // dodajemo interface KeyListener i this(klasa koja implementira dati interface)
		setFocusable(true);
		setFocusTraversalKeysEnabled(true); // ukoliko strelica ne radi, ukljuciti ih pomocu TAB
		vreme = new Timer(kasnjenje,this);
		vreme.start();
	}
	
	// podesavanje pocetne pozicije zmije!
	protected void PozZmije(Graphics g) {
		if(pocPoz == 0) {
			x[0] = 65;
			x[1] = 49;
			x[2] = 33;
			
			y[0] = 140;
			y[1] = 140;
			y[2] = 140;
			
		}
		if(x[0] == 65 && y[0] == 140) {
			glavaDesno = new ImageIcon(getClass().getClassLoader().getResource("glavaDesno.png")); // razlog exportovanja slika u .jar app
			glavaDesno.paintIcon(this, g, x[0], y[0]);
		}
		for(int i = 0; i <  duzina_zmije ;i++) {
			if(i == 0 && desno) {
				glavaDesno = new ImageIcon(getClass().getClassLoader().getResource("glavaDesno.png"));
				glavaDesno.paintIcon(this, g, x[i], y[i]);
			}
			if(i == 0 && levo) {
				glavaLevo = new ImageIcon(getClass().getClassLoader().getResource("glavaLevo.png"));
				glavaLevo.paintIcon(this, g, x[i], y[i]);
			}
			if(i == 0 && dole) {
				glavaDole = new ImageIcon(getClass().getClassLoader().getResource("glavaDole.png"));
				glavaDole.paintIcon(this, g, x[i], y[i]);
			}
			if(i == 0 && gore) {
				glavaGore = new ImageIcon(getClass().getClassLoader().getResource("glavaGore.png"));
				glavaGore.paintIcon(this, g, x[i], y[i]);
			}
			if(i!=0) {
				telo = new ImageIcon(getClass().getClassLoader().getResource("telo.png"));
				telo.paintIcon(this, g, x[i], y[i]);
			}
		}
		
		pozJabuke(g);
		GameOver(g);
	}
	
	// pozicioniranje jabuke i povecanje zmije
	public void pozJabuke(Graphics g) {
		jabuka = new ImageIcon(getClass().getClassLoader().getResource("jabuka.png"));
		
		if(x_jab[xpoz] == x[0] && y_jab[ypoz] == y[0])
		{
			rezultat += 20;
			duzina_zmije++;
			if(duzina_zmije >=15) {
				vreme.stop();
				vreme = new Timer(kasnjenje - 40,this);
				vreme.start();
				g.setColor(Color.RED);
				g.setFont(new Font("arial",Font.BOLD,14));
				g.drawString("  II",70, 560);
			}
		if(duzina_zmije >=30) {
				vreme.stop();
				vreme = new Timer(kasnjenje - 80,this);
				vreme.start();
				g.setColor(Color.RED);
				g.setFont(new Font("arial",Font.BOLD,14));
				g.drawString("  III",70, 560);
			}
			xpoz = random.nextInt(32);
			ypoz = random.nextInt(23);
		}
		jabuka.paintIcon(this, g, x_jab[xpoz], y_jab[ypoz]);
		
	}
	
	// game over ( glava  udara u telo )
	public void GameOver(Graphics g) {
		for(int i = 1; i < duzina_zmije;i++) {  	// prolazi kroz zglobove(telo)
			if(x[i] == x[0] && y[i] == y[0]) 		// u slucaju da je u dodiru sa glavom ti "napravi" gresku
			{
				desno = false;
				levo = false;
				gore = false;
				dole = false;
				
				ProveriRezultat();   // poziv funkcije za kreiranje .dat fajla 
				
				g.setColor(Color.decode("#e8eaf6"));
				g.setFont(new Font("arial", Font.BOLD,48));
				g.drawString("Game Over", 157, 300);
				
				g.setFont(new Font("arial", Font.BOLD,24));
				g.drawString("R - ", 225, 340);
				ImageIcon restart = new ImageIcon(getClass().getClassLoader().getResource("restart.png"));
				restart.paintIcon(this, g, 300, 320);
				
				g.setFont(new Font("arial", Font.BOLD,24));
				g.drawString("P - ", 225, 380);
				ImageIcon pause = new ImageIcon(getClass().getClassLoader().getResource("pause.png"));
				pause.paintIcon(this, g, 300, 360);
				
				g.setFont(new Font("arial", Font.BOLD,24));
				g.drawString("Space - ", 170, 420);
				ImageIcon play = new ImageIcon(getClass().getClassLoader().getResource("play.png"));
				play.paintIcon(this, g, 300, 400);
				
			}
		}
	}
	
	public void paint(Graphics g) {

		// slika naslov
		naslov = new ImageIcon(getClass().getClassLoader().getResource("Naslov.PNG"));
		naslov.paintIcon(this, g, -30, 5);
		
		// postavi polje kretanja
		g.setColor(Color.decode("#000a12"));
		g.fillRect(32, 140, p_sirina, p_visina);
		
		PozZmije(g);    // poziv funkcije za pocetnu poziciju zmije
		
		// duzina zmije
		g.setColor(Color.decode("#757575"));
		g.setFont(new Font("arial",Font.BOLD,14));
		g.drawString("Duzina: ", 178, 137);
		g.setColor(Color.RED);
		g.drawString(" "+duzina_zmije, 228, 137);
		
		// rezultat
		g.setColor(Color.decode("#757575"));
		g.drawString("Rezultat: ", 259, 137);
		g.setColor(Color.RED);
		g.drawString(" "+rezultat, 320, 137);
				
		// najbolji rezultat
		if(najbolji_rezultat.equals("niko:0"))
		{
			najbolji_rezultat = this.GetnajboljiRezultat();
		}
		g.setColor(Color.decode("#757575"));
		g.drawString("Najbolji rezultat: ", 355, 137);
		g.setColor(Color.decode("#039be5"));
		g.drawString(" "+ najbolji_rezultat, 470, 137);
		
		// brzina igre
		g.setColor(Color.decode("#757575"));
		g.drawString("Level: ", 33, 560);
		g.setColor(Color.RED);
		g.drawString("  I",70, 560);
		
		// autor 
		g.setColor(Color.decode("#5a9216"));
		g.setFont(new Font("arial",Font.BOLD,12));
		g.drawString("© Marko Djordjevic 12/2018",400, 560);
	}
	
	// izcitavaje najboljeg rezultata iz tekstualnog fajla Rezultat.dat
												// fajl je .dat jer njime korisnik ne moze da manipulise kao sa .txt
	protected String GetnajboljiRezultat() {
		
		FileReader file = null;
		BufferedReader buffer = null;
		try {
			file = new FileReader("Rezultat.dat");  // odjednom cita dva bajta i poboljsanje je prethodne verzije preko Scanner koja se koristila
			buffer = new BufferedReader(file); // uzima FileReader i cuva ga u memoriji(RAM), sve dok ne bude pogodan za njegovu obradu
			return buffer.readLine();		// i umesto da se citaju dva bajta, sada se preko BufferedReader - a cita ceo red, i proces tece mnogo brze.
		}
		catch(Exception e) {
			return "niko:0";      // u slucaju da nije otvoren fajl vrati mi 0 kao rezultat.
		}
		finally   // izvrsava se kada se izvrsi try blok
		{
			try {
				if(buffer != null)
				buffer.close();
			}
			catch(Exception e) {
				e.printStackTrace();    // javlja gresku koja se dogodila i gde se u kodu dogodila ( liniju koda )
			}
		}
	}
	
	protected void ProveriRezultat()
	{
		// rezultat > najbolji_rezultat
			// format je:  niko:0 .
		//  posto poredjujem int i String, moram String da razdvojim(split) na dva dela
		// i kastujem drugi deo u Integer.
		
		
		String m[] = najbolji_rezultat.split(":", 2);  // razdvoji string na dva dela kad naidjes na ":"
		
		if(rezultat > Integer.parseInt(m[1])) // uzmi drugi deo tj broj u ovom slucaju
		{
			// nek korisnik unese svoje ime i postavi svoj rezultat
			ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("slikaOptionPane.png"));
			String ime = (String) JOptionPane.showInputDialog(null,"Unesite vase ime?","NAJBOLJI REZULTAT",JOptionPane.INFORMATION_MESSAGE,icon,null,"");
			
			najbolji_rezultat = ime + ":" + rezultat;
		}
		
		// kreiranja fajla Rezultat.dat, ukoliko ne postoji
		File file_rez = new File("Rezultat.dat");
		if(!file_rez.exists())  {          // ako Rezultat.dat ( fajl ) ne postoji 
			try {
				file_rez.createNewFile(); // kreiraj Rezultat.dat ( fajl )
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
		//upis najboljeg rezultata u fajl
		FileWriter fupis = null;
		BufferedWriter bupis = null;
		try {
			fupis = new FileWriter("Rezultat.dat");
			bupis = new BufferedWriter(fupis);
			bupis.write(this.najbolji_rezultat);
		}
		catch(Exception e) {
			e.getStackTrace();
		}
		finally {
			try {
				if(bupis != null)
					bupis.close();
			}
			catch(Exception e) {
				e.getStackTrace();
			}
		}
		
		
	}
	
	// postavke za kretanje kroz takozvani "zid" polja
	protected void kretanje() {
		if(desno) {
			for(int i = duzina_zmije; i > 0;i--) {   // poz y ose
				y[i] = y[i-1];
			}
			for(int i = duzina_zmije; i>= 0;i--){ // poz x ose
				if(i ==0)
				x[i] = x[i] + 16;
				else                       // kretanje zmije po x osi
					x[i] = x[i-1];
				if(x[i] > 529)   // provera ako je veca od polja da vrati zmiju na pocetak polja( s leve strane) 
					x[i] = 33;
			}
			repaint();      // automatski pozivi paint fukn , kao rekurzija slicno !!
			}
		if(levo) {
			for(int i = duzina_zmije; i > 0;i--) {   
				y[i] = y[i-1];
			}
			for(int i = duzina_zmije; i>= 0;i--){ 
				if(i ==0)
				x[i] = x[i] - 16;
				else                      
					x[i] = x[i-1];
				if(x[i] < 33)    
					x[i] = 529;
			}
			repaint();      
		}
		if(gore) {
			for(int i = duzina_zmije; i > 0;i--) {   
				x[i] = x[i-1];
			}
			for(int i = duzina_zmije; i>= 0;i--){ 
				if(i ==0)
				y[i] = y[i] - 16;
				else                       
					y[i] = y[i-1];
				if(y[i] < 140)    
					y[i] = 524;
			}
			repaint();      
		}
		if(dole) {
			for(int i = duzina_zmije; i > 0;i--) {   
				x[i] = x[i-1];
			}
			for(int i = duzina_zmije; i>= 0;i--){ 
				if(i ==0)
					y[i] = y[i] + 16;
				else                       
					y[i] = y[i-1];
				if(y[i] > 524)    
					y[i] = 140;
		}
			repaint();      
		}
	}
	
	// action se automatski poziva, kada je u konstruktoru vreme.start(); ( krenulo ) !!!
	@Override
	public void actionPerformed(ActionEvent e) {
		vreme.start();
		kretanje();

	}

	@Override
	public void keyTyped(KeyEvent e) {
	
		
	}

	// tasteri za kretanje kroz igru
	@Override
	public void keyPressed(KeyEvent e) {
		

		// restart igre
		if(e.getKeyCode() == KeyEvent.VK_R)
		{
			kasnjenje = 180;
			duzina_zmije = 3;
			pocPoz = 0;
			rezultat = 0;
			xpoz = random.nextInt(32);
			ypoz = random.nextInt(23);
			repaint();
		}
		
		// pauza igre
		if(e.getKeyCode() == KeyEvent.VK_P) {
			vreme.stop();
		}
		
		// nastavak igre
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			vreme.start();
			repaint();
		}
		
		if(e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			pocPoz++;
			desno = true;
			if(!levo) {
				desno = true;
			}
			else {
				desno = false;
				levo = true;
			}

			
			gore = false;
			dole = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			pocPoz++;
			levo = true;
			if(!desno) {
				levo = true;
			}
			else {
				levo = false;
				desno = true;
			}
		
			gore = false;
			dole = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_UP)
		{
			pocPoz++;
			gore = true;
			if(!dole) {
				gore = true;
			}
			else {
				dole = true;
				gore = false;
			}
			desno = false;
			levo  = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			pocPoz++;
			dole = true;
			if(!gore) {
				dole = true;
			}
			else {
				gore = true;
				dole = false;
			}
			desno = false;
			levo  = false;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		
	}

}
