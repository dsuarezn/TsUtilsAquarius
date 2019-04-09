package co.gov.ideam.dhime.modificador.series.automated;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import co.gov.ideam.dhime.comparador.engine.EjecucionesLocationsMethods;
import co.gov.ideam.dhime.comparador.webapp.dto.EstacionEtiqueta;
import co.gov.ideam.dhime.comparador.webapp.dto.TSParametro;
import co.gov.ideam.dhime.comparador.webapp.dto.TSParametroEstadistica;
import co.gov.ideam.dhime.modificador.series.EjecucionesLocationsMethodsModif;
import co.gov.ideam.dhime.modificador.series.TimeSeriesConfigModifier;

import java.util.concurrent.TimeUnit;

public class AutoTimeSeriesDerivedCreator {
	
	private final String baseUrl="http://172.16.50.54/AQUARIUS/";
	@Test
	public void llamarCreacionStadistica(){
		statisticalTimeSeries(EjecucionesLocationsMethodsModif.listaEstacionEtiquetaPruebaDerivada(), TimeSeriesConfigModifier.listaDeTsParametrosDerivadas());
	}
	
	public void statisticalTimeSeries(List<EstacionEtiqueta> listaEstaciones, Map<String,TSParametroEstadistica> directorioEtiquetas){
		WebDriver driver=getFireFoxDriver();
		driver.get(baseUrl);		
		String login="admin";
		String password="dh1m3w3b2018*";
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		//Se hace login a la pagina
		login(driver, login,password);
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		for (EstacionEtiqueta estacionEtiqueta : listaEstaciones) {
			//Se inicia a recorrer el array de estaciones colocando la estacion en la casilla de busqueda
			WebElement lookup=driver.findElement(By.name("locationLookupControl"));
			lookup.sendKeys(estacionEtiqueta.getEstacion());
			driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			lookup.sendKeys(Keys.ENTER);			
			//Se verifica que el espacio de estaciones tenga una estacion almenos
			WebElement sortableList=driver.findElement(By.xpath("/html//div[@id='locationsContainer']"));
			List<WebElement> childs = sortableList.findElements(By.xpath(".//*"));
			
			if(childs.size()>0){
				//Si tiene almenos una estacion se da click al link de la estacion
				WebElement loclist=driver.findElement(By.partialLinkText("["+estacionEtiqueta.getEstacion()+"]"));
				loclist.click();
				driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
				//Se da click a icono de creacion de opciones de la estacion				
				WebElement labelfilter=driver.findElement(By.xpath("//li[@id='locationListItemId0']/div[@role='button']/div/span[2]/i[1]"));
				labelfilter.click();
				driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
				//Se da click en el link de nueva serie temporal derivada
				WebElement itemSerieDev=driver.findElement(By.partialLinkText("Nueva Serie temporal derivada"));
				itemSerieDev.click();
				driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
				//Se da click en el boton de editar periodo de procesamiento
				WebElement buttonPeriodProcess=driver.findElement(By.xpath("/html//div[@role='dialog']//form[@name='timeSeriesForm']//uib-accordion[@class='']/div[@role='tablist']/div[1]/div[@role='tabpanel']//button[@title='Editar Período de Procesamiento']"));
				buttonPeriodProcess.click();
				driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
				
				//Se da click en el dropdown del tipo de proceso
				WebElement tipoProceso=driver.findElement(By.id("processType"));
				tipoProceso.click();
				
				driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
				WebElement procesoEstadisticaCalculos=driver.findElement(By.xpath("//select[@id='processType']/option[@label='Estadística/Cálculos']"));
				procesoEstadisticaCalculos.click();
				
//				driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
//				WebElement procesoEstadisticaCalculos=driver.findElement(By.xpath("//select[@id='processType']/option[@label='Estadística/Cálculos']"));
//				procesoEstadisticaCalculos.click();
				
//				
				
				
			}
			
			
			
			
			
			
		}
			
	}
	
	private void login(WebDriver driver, String login, String password){
		WebElement langDrop=driver.findElement(By.id("LanguageDropDownList"));
		langDrop.click();
		WebElement optionSel=driver.findElement(By.xpath("//select[@id='LanguageDropDownList']/option[@value='es']"));
		optionSel.click();			
		WebElement loginField=driver.findElement(By.id("UserName1"));
		WebElement loginPassword=driver.findElement(By.id("Password1"));
		WebElement submit=driver.findElement(By.id("LoginBtn"));
		loginField.sendKeys(login);
		loginPassword.sendKeys(password);	
		submit.click();
	}
	
	private WebDriver getFireFoxDriver(){
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference( "intl.accept_languages", "es" ); 
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("geckodriver/geckodriver.exe").getFile());		
		System.setProperty("webdriver.gecko.driver",file.getAbsolutePath() );	
		FirefoxOptions options=new FirefoxOptions();
		options.setProfile(profile);
		WebDriver driver = new FirefoxDriver(options);
		return driver;
	}
	
	private WebDriver getChromeDriver(){
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--lang=es");
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("chromedriver/chromedriver.exe").getFile());		
		System.setProperty("webdriver.chrome.driver",file.getAbsolutePath() );		
		WebDriver driver = new ChromeDriver(options);
		return driver;
	}
	
	

}
