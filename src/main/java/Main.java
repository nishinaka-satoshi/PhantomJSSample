import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Optional;

/**
 * Created by satoshi nishinaka on 2016/12/15.
 */
public class Main {
  
  private static final String PHANTOMJS_PATH = System.getenv("PHANTOM_JS_PATH");
  private static final String[] PHANTOM_ARGS =
      new String[]{
          "--webdriver-loglevel=NONE",
          "--load-images=no"
      };
  private static final int TIMEOUT_SECONDS = 30;

  public static void main(String[] args) {
    Main testEngine = new Main();
    testEngine.check("http://www.yahoo.co.jp");
  }

  private PhantomJSDriver initDriver() {
    // set Capabilities
    DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
    capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, PHANTOM_ARGS);
    capabilities.setCapability("phantomjs.page.settings.resourceTimeout", TIMEOUT_SECONDS);
    capabilities.setJavascriptEnabled(true);

    System.setProperty("phantomjs.binary.path", PHANTOMJS_PATH);
    PhantomJSDriver driver = new PhantomJSDriver(capabilities);
    return driver;
  }


  public void check(String url) {

    long processStartTime = System.currentTimeMillis();
    PhantomJSDriver driver = null;

    try {
      driver = this.initDriver();
      driver.get(url); // access to specified URL

      waitForLoad(driver);

      // Get values of Navigation Timing
      long startTime = (Long) driver.executeScript("return window.performance.timing.navigationStart");
      long loadEndTime = (Long) driver.executeScript("return window.performance.timing.loadEventEnd");
      long responseEndTime = (Long) driver.executeScript("return window.performance.timing.responseEnd");

      Optional<WebElement> tag = this.findTag(driver);
      if (tag.isPresent()) {
        System.out.println("Find !!");
      } else {
        System.out.println("Could not find ...");
      }
      System.out.format("Response Time : %d\n", responseEndTime - startTime);
      System.out.format("PageLoad Time : %d\n", loadEndTime - startTime);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.format("Exception occurred. %s %s. %d sec.\n", e.getClass().getName(), e.getMessage(), System.currentTimeMillis() - processStartTime);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }

    if (driver != null) {
      System.out.println("Destroy phantomJsDriver.");
      driver = null;
    }
  }

  private void waitForLoad(PhantomJSDriver driver) throws Exception {
    ExpectedCondition<Boolean> pageLoadCondition = new
        ExpectedCondition<Boolean>() {
          public Boolean apply(WebDriver driver) {
            return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
          }
        };
    try {
      WebDriverWait wait = new WebDriverWait(driver, TIMEOUT_SECONDS);
      wait.until(pageLoadCondition);
    } catch(TimeoutException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  private Optional<WebElement> findTag(PhantomJSDriver driver) {
    if (driver == null) {
      System.out.println("Driver is not set...");
      return Optional.empty();
    }
    try {
      WebElement element = driver.findElement(By.id("masthead"));
      return Optional.ofNullable(element);
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
