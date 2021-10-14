package com.interview.balancescalegame;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Problem: Given 9 bars(0,1,2,3,4,5,6,7,8,9), all the bars has same weight except one fake bar which has less weight. Our task is to find the fake bar.
 * Algorithm:
 * 1. Group the bars into three sets. ((0,1,2) , (3,4,5) ,(6,7,8) in this case)
 * 2. Test two sets at first. Put one set in left bowl and second set in right bowl.((0 in first cell ,1 in second cell,2 in third cell) of left bowl and (3 in first cell,4 in first cell,5 in third cell) of right bowl)
 * 3. Weigh the above two sets and get the result sign ("=", "<" ,">").
 * 4. Analyse the output depending on the result sign:
 *    -  if result sign is "=" then the first two sets do not contain fake bar as there weights are equal and the remaining test should be carried with the thirst set(6,7,8) only.
 *    -  if result sign is "<" then the first set(left bowl) weighs lesser than second set(right bowl) so the remaining tests should be carried with the first set(0,1,2) only.
 *    -  if result sign is ">" then the second set(right bowl) weighs lesser than first set(left bowl) so the remaining tests should be carried with the second set(3,4,5) only.
 * 5. Reset the previously entered left and right bowl values.
 * 6. Get the set(3 bars) from step 4 . Compare two bars from the remaining 3 bars. Enter one bar in first cell of left bowl and one bar in first cell of right bowl and weigh them
 * 7. Analyse the result depending on the result sign:
 *    -  if result sign is "=" then left bar and right bar are equal and the remaining bar is the fake bar.
 *    -  if result sign is "<" then left bar is fake bar as it weighs less than right bar.
 *    -  if result sign is ">" then right bar is fake bar as it weighs less than left bar.
 * 8. Select the fake bar found in Step 7 and click it.
 * 9. "Yay! You find it!" alert message is sent if answer is correct  else "Oops! Try Again!" alert message is sent.
 */


public class FakeBar {

    public static void main(String[] args) {

        //Set browser properties and open the same
        //System.setProperty("webdriver.chrome.driver","/Users/smruti/Desktop/FetchRewards_Assignment/chromedriver");
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        // set the website and open it
        String webURL = "http://ec2-54-208-152-154.compute-1.amazonaws.com/";
        driver.get(webURL);

        //Round 1
        //Find left bowl and right bowl elements
        //enter the first set of bars in left bowl and second set in right bowl
        WebElement leftBowl0 = driver.findElement(By.id("left_0"));
        leftBowl0.sendKeys("0");
        WebElement leftBowl1 = driver.findElement(By.id("left_1"));
        leftBowl1.sendKeys("1");
        WebElement leftBowl2 = driver.findElement(By.id("left_2"));
        leftBowl2.sendKeys("2");
        WebElement rightBowl0 = driver.findElement(By.id("right_0"));
        rightBowl0.sendKeys("3");
        WebElement rightBowl1 = driver.findElement(By.id("right_1"));
        rightBowl1.sendKeys("4");
        WebElement rightBowl2 = driver.findElement(By.id("right_2"));
        rightBowl2.sendKeys("5");

        //find weight controls and click
        WebElement weighBtn = driver.findElement(By.id("weigh"));
        weighBtn.click();

        //wait for the result sign to be displayed
        WebDriverWait wait = new WebDriverWait(driver,10);
        boolean visible = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div li:nth-child(1)"))) != null;
        if(!visible) throw new RuntimeException("Timeout");

        //find result element and check the sign
        //Take result from result sign , analyse the sign and make decision
        WebElement resultSignWebElement = driver.findElement(By.cssSelector("div.result button"));
        String resultSign = resultSignWebElement.getText();

        int startNum ;
        if(resultSign.equals("="))
            startNum = 6;
        else if(resultSign.equals("<"))
            startNum = 0;
        else if(resultSign.equals(">"))
            startNum = 3;
        else throw new RuntimeException("Unknown Sign");

       //Reset the previous bowl values
        WebElement reset = driver.findElement(By.xpath("//button[contains(text(),'Reset')]"));
        reset.click();

        //Round 2
        //Enter the gold bar set from the above selected group and weigh them
        leftBowl0.sendKeys(String.valueOf(startNum));
        rightBowl0.sendKeys(String.valueOf(startNum+1));
        weighBtn.click();

        //wait for the result sign to be displayed and get the result sign
        visible = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div li:nth-child(2)"))) != null;
        if(!visible) throw new RuntimeException("Timeout");
        resultSign = resultSignWebElement.getText();

        //Take result from result sign , analyse the sign and make decision
        int fakeGoldBar;
        if(resultSign.equals("="))
            fakeGoldBar = startNum + 2;
        else if(resultSign.equals("<"))
            fakeGoldBar = startNum;
        else if(resultSign.equals(">"))
            fakeGoldBar = startNum +1;
        else throw new RuntimeException("Unknown sign");

        System.out.println("No of weighings is 2");

        WebElement fakeCoin = driver.findElement(By.cssSelector("div.coins button[id$='" + fakeGoldBar +"']"));
        fakeCoin.click();

       //Check the alert message to see if fake bar is found
        Alert alert=driver.switchTo().alert();
        String expectedMessage = "Yay! You find it!";
        if(expectedMessage.equalsIgnoreCase(alert.getText())){
          System.out.println(alert.getText());
       }else{
            System.out.println("Oops! Try Again!");
        }
       alert.accept();

        driver.quit();

    }
}
