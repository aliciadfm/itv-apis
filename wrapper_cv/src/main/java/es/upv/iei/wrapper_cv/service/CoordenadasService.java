package es.upv.iei.wrapper_cv.service;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CoordenadasService {

    public static double[] obtenerLatLon(String direccion) {

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {
            driver.get("https://www.coordenadas-gps.com/convertidor-de-coordenadas-gps");

            WebDriverWait waitCookies = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitCookies.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//p[@class='fc-button-label' and normalize-space()='Consentir']")
            )).click();

            WebElement input = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("address"))
            );
            input.clear();
            input.sendKeys(direccion + ", Comunidad Valenciana, España");

            ((JavascriptExecutor) driver).executeScript("codeAddress();");

            wait.until(d ->
                    !d.findElement(By.id("latitude")).getAttribute("value").isEmpty()
            );
            wait.until(d ->
                    !d.findElement(By.id("longitude")).getAttribute("value").isEmpty()
            );

            WebElement lat = driver.findElement(By.id("latitude"));
            WebElement lon = driver.findElement(By.id("longitude"));

            return new double[]{
                    Double.parseDouble(lat.getAttribute("value")),
                    Double.parseDouble(lon.getAttribute("value"))
            };

        } finally {
            driver.quit();
        }
    }

}
