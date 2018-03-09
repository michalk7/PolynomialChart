package zad2;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

public class ChartPanel extends JPanel implements MouseMotionListener{
	
	private HashMap<String, Double> chartCoordinates;
	private HashMap<String, JTextField> chartCoordinatesField;
	private JTextField fieldForPolynomial;
	private boolean paintChart;					//włącza obsułgę myszy tylko wtedy, gdy zostanie po raz pierwszy narysowany jakis uklad
	private boolean parseNewCoordinates;		//zabrania zmiany wspolrzednych ukladu w czasie rzeczywistym
	private boolean readNewPolynomial;			//zabrania zmiany wielomianu w czasie rzeczywistym
	private double[] wspolczynnikiPotegWiekszychOd1;
	private int[] potegiWiekszeOd1;
	private double wspolczynnikXDo1;
	private double liczbaBezX;
	private int yLast = -1;
	
	public ChartPanel(HashMap<String, JTextField> chartCoordinates, JTextField fieldForPolynomial) {
		this.chartCoordinates = new HashMap<>();
		this.chartCoordinatesField = chartCoordinates;
		this.fieldForPolynomial = fieldForPolynomial;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(parseCoordinates(chartCoordinatesField) && !fieldForPolynomial.getText().isEmpty()) {
			g.setColor(Color.BLACK);
			double yScale = (double)getHeight() / (chartCoordinates.get("ymax") + Math.abs(chartCoordinates.get("ymin")));
			double xScale = (double)getWidth() / (chartCoordinates.get("xmax") + Math.abs(chartCoordinates.get("xmin")));
			g.drawLine(0, (int)(yScale*chartCoordinates.get("ymax")), getWidth(), (int)(yScale*chartCoordinates.get("ymax")));
			g.drawLine((int)(Math.abs(chartCoordinates.get("xmin"))*xScale), 0, (int)(Math.abs(chartCoordinates.get("xmin"))*xScale), getHeight());
			if(readNewPolynomial) {
				readValuesFromPolynomial();
				readNewPolynomial = false;
			}	
			g.setColor(Color.BLUE);
			
			for(double i = chartCoordinates.get("xmin"); i <= chartCoordinates.get("xmax"); i+=0.001 ) { // tu nawet 0.001 bedzie ok po zmianiach
				double lx = (i-chartCoordinates.get("xmin"))/(chartCoordinates.get("xmax")-i);
				double xD = (lx*(double)getWidth())/(1.0+lx);
				int x = (int)xD;
				double yOs = calculatePolynomial(i);
				if(yOs >= chartCoordinates.get("ymin") && yOs <= chartCoordinates.get("ymax")) {
					double ly = (chartCoordinates.get("ymax")-yOs)/(yOs-chartCoordinates.get("ymin"));
					double yD = (ly*(double)getHeight())/(1.0+ly);
					int y = (int)yD;
					g.drawLine(x, y, x, y);
				}
			}
		}
	}
	
	public void setPaintChart(boolean paintChart) {
		this.paintChart = paintChart;
	}

	public void setParseNewCoordinates(boolean parseNewCoordinates) {
		this.parseNewCoordinates = parseNewCoordinates;
	}
	
	public void setReadNewPolynomial(boolean readNewPolynomial) {
		this.readNewPolynomial = readNewPolynomial;
	}
	
	private boolean parseCoordinates(HashMap<String, JTextField> chartCoordinates) {
		if(parseNewCoordinates) {
			try {
				double xmin = Double.parseDouble(chartCoordinates.get("xmin").getText());
				double xmax = Double.parseDouble(chartCoordinates.get("xmax").getText());
				double ymin = Double.parseDouble(chartCoordinates.get("ymin").getText());
				double ymax = Double.parseDouble(chartCoordinates.get("ymax").getText());
				if(xmin < 0 && ymin < 0 && xmax > 0 && ymax > 0) {
					this.chartCoordinates.put("xmin", xmin);
					this.chartCoordinates.put("xmax", xmax);
					this.chartCoordinates.put("ymin", ymin);
					this.chartCoordinates.put("ymax", ymax);
					parseNewCoordinates = false;
					return true;
				} else {
					return false;
				}
			} catch(NumberFormatException e) {
				return false;
			}
		} else {
			return true;
		}
	}
	
	private String getPolynomial(JTextField fieldForPolynomial) {
		String polynomial = fieldForPolynomial.getText();
		polynomial = polynomial.replaceAll("-x", "-1x");
		polynomial = polynomial.replaceAll("\\+x", "+1x");
		polynomial = polynomial.replaceAll("^x", "1x");
		return polynomial;
	}
	
	private void readValuesFromPolynomial() {
		String polynomial = getPolynomial(fieldForPolynomial);
		wspolczynnikXDo1 = 0;
		liczbaBezX = 0;
		Pattern xZPotega = Pattern.compile("x\\^");
		Matcher m = xZPotega.matcher(polynomial);
		int iloscXZPotegaWiekszaOd1 = 0;
		while(m.find()) {	//okreslamy wilkosc tablicy na potegi wieksze od 1 i ich wspolczynniki
			iloscXZPotegaWiekszaOd1++;
		}
		potegiWiekszeOd1 = new int[iloscXZPotegaWiekszaOd1];
		xZPotega = Pattern.compile("x\\^[0-9]+");
		m = xZPotega.matcher(polynomial);
		int licznik = 0;
		while(m.find()) {	//tu wpisujemy potegi do tablicy
			Pattern PotegiXWiekszychOd1 = Pattern.compile("[0-9]+");
			Matcher m2 = PotegiXWiekszychOd1.matcher(m.group());
			m2.find();
			potegiWiekszeOd1[licznik++] = Integer.parseInt(m2.group());
		}
		Pattern wspolczynnikiXZPotegaWiekszaOd1 = Pattern.compile("-?[0-9]+x\\^");
		Matcher m3 = wspolczynnikiXZPotegaWiekszaOd1.matcher(polynomial);
		licznik = 0;
		wspolczynnikiPotegWiekszychOd1 = new double[iloscXZPotegaWiekszaOd1];
		while(m3.find()) {	//tu wpisujemy wspolczynniki do odpowiednich poteg
			Pattern wspolczynnikiXZPotegaWiekszaOd1Wartosci = Pattern.compile("-?[0-9]+");
			Matcher m4 = wspolczynnikiXZPotegaWiekszaOd1Wartosci.matcher(m3.group());
			m4.find();
			wspolczynnikiPotegWiekszychOd1[licznik++] = Double.parseDouble(m4.group());
		}
		Pattern xDoPotegi1Oraz0 = Pattern.compile("-?[0-9]*x[-\\+][0-9]*");  //obsluzyc jeszcze samego x
		Matcher m5 = xDoPotegi1Oraz0.matcher(polynomial);
		if(m5.find()) {	//wyszukujemy wspolczynniki dla x^1 i x^0
			Pattern wspolczynnikiXDoPotegi1Oraz0 = Pattern.compile("-?[0-9]+");
			Matcher m6 = wspolczynnikiXDoPotegi1Oraz0.matcher(m5.group());
			if(m6.find()) {
				wspolczynnikXDo1 = Double.parseDouble(m6.group());
			} 
			if(m6.find()) {
				liczbaBezX = Double.parseDouble(m6.group());
			}
		} else {	//wyszukujemy wartosc x^0 jesli x^1 nie wystepuje w wielomianie
			Pattern wylacznieXDoPotegi0 = Pattern.compile("([+-]|\\A)[0-9]*[0-9]$");
			Matcher m7 = wylacznieXDoPotegi0.matcher(polynomial);
			if(m7.find()) {
				liczbaBezX = Double.parseDouble(m7.group());
			} else {
				Pattern wylacznieXDoPotegi1 = Pattern.compile("-?[0-9]*x$");
				Matcher m8 = wylacznieXDoPotegi1.matcher(polynomial);
				if(m8.find()) {
					String tmp = m8.group().replaceAll("x", "");
					wspolczynnikXDo1 = Double.parseDouble(tmp);
				}
			}
			
		}
	}
	
	private double calculatePolynomial(double x) {
		double wynik = 0;
		for(int i = 0; i < potegiWiekszeOd1.length; i++) {
			wynik += wspolczynnikiPotegWiekszychOd1[i]*(Math.pow(x, potegiWiekszeOd1[i]));
		}
		wynik += wspolczynnikXDo1*x +liczbaBezX;
		return wynik;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Graphics g = this.getGraphics();
		if(paintChart && parseCoordinates(chartCoordinatesField)) {
			double zX = (double)e.getX()/(double)(getWidth() - e.getX());
			//xEkranu
			//-------		= zX
			//W - xEkranu
			double xOs = (zX*chartCoordinates.get("xmax") + chartCoordinates.get("xmin"))/(1.0 + zX);
			//zX * xMax + xMin
			//----------------	= xNaUkladzieWspolrzednych
			// 1 + zX
			double yOs = calculatePolynomial(xOs);	//obliczenie y dla x myszki
				
			
			if(yLast == e.getY()) { //zapobiegniecie sytuacji w ktorej dla roznych x o tym samym y
								//nie znika linia rzutujaca punkt na os X, dzieje sie tak, gdy nie oderwiemy kursora od wykresu
				this.paintImmediately(0, 0, getWidth(), getHeight());
			}
			
			if(yOs >= chartCoordinates.get("ymin") && yOs <= chartCoordinates.get("ymax")) {	//unikamy zbednych obliczen jak y nie jest w przedziale do rysowania 
				double ly = (chartCoordinates.get("ymax")-yOs)/(yOs-chartCoordinates.get("ymin"));
				// yOs = yMyszkiNaUkladzieWspolrzednych
				// yMax - yOs
				//------------  = ly
				// yOs - yMin
				double yD = (ly*(double)getHeight())/(1.0+ly);
				// ly * H
				//---------	= yD = yEkranowyDouble
				// 1 + ly
				int y = (int)yD;  //yEkranowyInt 
				
				double lx = (0-chartCoordinates.get("xmin"))/(chartCoordinates.get("xmax")-0);
				double xD = (lx*(double)getWidth())/(1.0+lx);
				int xUkladu = (int)xD;	// x Ekranowy dla x = 0
				
				double lyUkladu = (chartCoordinates.get("ymax")-0)/(0-chartCoordinates.get("ymin"));
				double yDUkladu = (lyUkladu*(double)getHeight())/(1.0+lyUkladu);
				int yUkladu = (int)yDUkladu; // y Ekranowy dla y = 0
				
				if(e.getY() == y) {		//jesli sa rowne, to kursor myskzi jest na wykresie
					g.setColor(Color.RED);
					if(e.getX() < xUkladu) {	// poprawne rysowanie lini dla II i III cwiartki ukladu
						g.drawLine(xUkladu - 1, e.getY(), e.getX(), e.getY());
					} else {
						g.drawLine(xUkladu + 1, e.getY(), e.getX(), e.getY());
					}
					if(e.getY() < yUkladu) {	// poprawne rysowanie lini dla III i IV cwiratki ukladu
						g.drawLine(e.getX(), e.getY() + 1, e.getX(), yUkladu - 1);
					} else {
						g.drawLine(e.getX(), e.getY(), e.getX(), yUkladu + 1);
					}
					g.setColor(Color.BLACK);
					//BigDecimal zeby uzyskac zaokraglenie
					BigDecimal xRoundVal = new BigDecimal(xOs);
					xRoundVal = xRoundVal.setScale(2, RoundingMode.HALF_UP);
					g.drawString("" + xRoundVal.doubleValue(), e.getX() + 1, yUkladu - 1);
					BigDecimal yRoundVal = new BigDecimal(yOs);
					yRoundVal = yRoundVal.setScale(2, RoundingMode.HALF_UP);
					g.drawString("" + yRoundVal.doubleValue(), xUkladu + 1, e.getY() - 1);
					yLast = e.getY();
				} else {
					this.repaint();		//do skasowania poprzednich lini
				}
			} else {
				this.repaint();		//do skasowanie poprzednich lini, musi byc rowniez tutaj, bo gdy bardzo szybko przesuniemy kursor poza
									//zakres w ktorym y nalezy do (yMin, yMax), to poprzednie linie sie nie usuna
			}
		} 
	}
}
