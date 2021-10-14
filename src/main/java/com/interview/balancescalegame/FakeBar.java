package com.interview.balancescalegame;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Problem: Given 9 bars(0,1,2,3,4,5,6,7,8,9), all the bars has same weight except one fake bar which has less weight. Our task is to find the fake bar.
 * Algorithm:
 * 1. Open website.
 * 2. Group the bars into three sets. ((0,1,2) , (3,4,5) ,(6,7,8) in this case)
 * 3. Test two sets at first. Insert one set in left bowl and second set in right bowl.((Insert 0 in first cell ,1 in second cell,2 in third cell) of left bowl and (Insert 3 in first cell,4 in first cell,5 in third cell) of right bowl)
 * 4. Click Weigh and weigh the above two sets and get the result sign ("=", "<" ,">").
 * 5. Get the result sign and analyse the output depending on the result sign:
 *    -  if result sign is "=" then the first two sets do not contain fake bar as there weights are equal and the remaining test should be carried with the third set(6,7,8) only.
 *    -  if result sign is "<" then the first set(left bowl) weighs lesser than second set(right bowl) so the remaining tests should be carried with the first set(0,1,2) only.
 *    -  if result sign is ">" then the second set(right bowl) weighs lesser than first set(left bowl) so the remaining tests should be carried with the second set(3,4,5) only.
 * 6. Click Reset and reset the previously entered left and right bowl values.
 * 7. Get the set(3 bars) from step 4 . Compare two bars from this set. Enter one bar in first cell of left bowl and one bar in first cell of right bowl and weigh them(Click Weigh)
 * 8. Analyse the result depending on the result sign:
 *    -  if result sign is "=" then left bar and right bar are equal and the remaining bar is the fake bar.
 *    -  if result sign is "<" then left bar is fake bar as it weighs less than right bar.
 *    -  if result sign is ">" then right bar is fake bar as it weighs less than left bar.
 * 9.  Select the fake bar found in Step 8 and click it.
 * 10. "Yay! You find it!" alert message is sent if answer is correct  else "Oops! Try Again!" alert message is sent.
 * 11. Display the List of Weighings and Number of Weighings
 */


public class FakeBar {

    public static void main(String[] args) throws InterruptedException {

        //Open browser
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        //Set the website and open it
        String webURL = "http://ec2-54-208-152-154.compute-1.amazonaws.com/";
        driver.get(webURL);

        int noOfWeighings =0;
        String startNum, nextNum, endNum, fakeGoldBar;
        //Controls
        WebElement weighBtn = driver.findElement(By.id("weigh")); // weight button
        WebElement resultSignWebElement = driver.findElement(By.cssSelector("div.result button")); // result sign
        WebElement reset = driver.findElement(By.xpath("//button[contains(text(),'Reset')]")); // reset button

        //Round 1
        //Find left bowl and right bowl elements
        //enter the first set of bars in left bowl and second set in right bowl
        WebElement leftBowl0 = driver.findElement(By.id("left_0"));
        leftBowl0.sendKeys(driver.findElement(By.id("coin_0")).getText());
        WebElement leftBowl1 = driver.findElement(By.id("left_1"));
        leftBowl1.sendKeys(driver.findElement(By.id("coin_1")).getText());
        WebElement leftBowl2 = driver.findElement(By.id("left_2"));
        leftBowl2.sendKeys(driver.findElement(By.id("coin_2")).getText());
        WebElement rightBowl0 = driver.findElement(By.id("right_0"));
        rightBowl0.sendKeys(driver.findElement(By.id("coin_3")).getText());
        WebElement rightBowl1 = driver.findElement(By.id("right_1"));
        rightBowl1.sendKeys(driver.findElement(By.id("coin_4")).getText());
        WebElement rightBowl2 = driver.findElement(By.id("right_2"));
        rightBowl2.sendKeys(driver.findElement(By.id("coin_5")).getText());

        //Click on the weight button
        weighBtn.click();
        noOfWeighings++;

        //wait for the result sign to be displayed
        WebDriverWait wait = new WebDriverWait(driver,10);
        boolean visible = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div li:nth-child(1)"))) != null;
        if(!visible) throw new RuntimeException("Timeout");

        //Get the result from result sign , analyse the sign and make decision
        String resultSign = resultSignWebElement.getText();
        if(resultSign.equals("=")) {
            startNum = driver.findElement(By.id("coin_6")).getText();
            nextNum = driver.findElement(By.id("coin_7")).getText();
            endNum =  driver.findElement(By.id("coin_8")).getText();
        }
        else if(resultSign.equals("<")) {
            startNum = driver.findElement(By.id("coin_0")).getText();
            nextNum = driver.findElement(By.id("coin_1")).getText();
            endNum =  driver.findElement(By.id("coin_2")).getText();
        }
        else if(resultSign.equals(">")) {
            startNum = driver.findElement(By.id("coin_3")).getText();
            nextNum =  driver.findElement(By.id("coin_4")).getText();
            endNum =  driver.findElement(By.id("coin_5")).getText();
        }
        else throw new RuntimeException("Unknown Sign");

        //Reset previous bowl values
        reset.click();

        //Round 2
        //Enter the gold bar set from the above selected group and weigh them
        leftBowl0.sendKeys(startNum);
        rightBowl0.sendKeys(nextNum);
        weighBtn.click();
        noOfWeighings++;

        //wait for the result sign to be displayed and get the result sign
        visible = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div li:nth-child(2)"))) != null;
        if(!visible) throw new RuntimeException("Timeout");
        resultSign = resultSignWebElement.getText();

        //Get result from result sign , analyse the sign and make decision
        if(resultSign.equals("="))
            fakeGoldBar = endNum;
        else if(resultSign.equals("<"))
            fakeGoldBar = startNum;
        else if(resultSign.equals(">"))
            fakeGoldBar = nextNum;
        else throw new RuntimeException("Unknown sign");

        WebElement fakeCoin = driver.findElement(By.cssSelector("div.coins button[id$='" + fakeGoldBar +"']"));
        fakeCoin.click();
        //Adding 2 sec o sleep to see the alert message clearly
        Thread.sleep(2000);

       //Check the alert message to see if fake bar is found
        Alert alert=driver.switchTo().alert();
        String expectedMessage = "Yay! You find it!";
        if(expectedMessage.equalsIgnoreCase(alert.getText())){
          System.out.println(alert.getText());
       }else{
            System.out.println("Oops! Try Again!");
        }
       alert.accept();
        //Adding 2 sec o sleep to see the alert message clearly
        Thread.sleep(2000);

        System.out.println("First Weighing is:  " + driver.findElement(By.cssSelector("div li:nth-child(1)")).getText());
        System.out.println("Second Weighing is: " + driver.findElement(By.cssSelector("div li:nth-child(2)")).getText());
        System.out.println("Total number of Weighings made: " + noOfWeighings);

        driver.quit();

    }
}
