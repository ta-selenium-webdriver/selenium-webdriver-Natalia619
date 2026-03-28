package com.softserve.academy;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreenCityNegativeRegistrationTest {
    private static WebDriver driver;

    @BeforeAll
    static void setUp() {
        ChromeOptions options = new ChromeOptions();
        if (System.getenv("GITHUB_ACTIONS") != null) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }

        driver = WebDriverManager.chromedriver().capabilities(options).create();
        driver.manage().window().maximize();
    }

    @BeforeEach
    void openRegistrationForm() throws InterruptedException {
        driver.navigate().to("https://www.greencity.cx.ua/#/greenCity");
        Thread.sleep(5000);
        driver.findElement(By.cssSelector(".header_sign-up-btn > span")).click();
        Thread.sleep(2000);
    }

    @Test
    @DisplayName("Invalid email format (without @) → email error")
    void shouldShowErrorForInvalidEmail() throws InterruptedException {
        typeEmail("invalid-email");
        typeUsername("ValidUsername");
        typePassword("ValidPass123!");
        typeConfirm("ValidPass123!");

        Thread.sleep(3000);

        assertEmailErrorVisible();
        assertSignUpButtonDisabled();
    }

    @Test
    @DisplayName("All fields empty → required errors shown")
    void shouldShowErrorsForAllEmptyFields() throws InterruptedException {
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("repeatPassword")).click();
        driver.findElement(By.id("email")).click();

        Thread.sleep(3000);

        assertEmailErrorVisible();
        assertUsernameErrorVisible();
        assertSignUpButtonDisabled();
    }

    @Test
    @DisplayName("Empty username → username required")
    void shouldShowErrorForEmptyUsername() throws InterruptedException {
        typeEmail("valid@email.com");
        typePassword("ValidPass123!");
        typeConfirm("ValidPass123!");
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("email")).click();

        Thread.sleep(3000);

        assertUsernameErrorVisible();
        assertSignUpButtonDisabled();
    }

    @Test
    @DisplayName("Short password (<8) → password rule error")
    void shouldShowErrorForShortPassword() throws InterruptedException {
        typeEmail("valid@email.com");
        typeUsername("ValidUsername");
        typePassword("testt");
        typeConfirm("testt");
        driver.findElement(By.id("email")).click();

        Thread.sleep(3000);

        assertPasswordErrorVisible();
        assertSignUpButtonDisabled();
    }

    @Test
    @DisplayName("Password with space → password rule error")
    void shouldShowErrorForPasswordWithSpace() throws InterruptedException {
        typeEmail("valid@email.com");
        typeUsername("ValidUsername");
        typePassword("Pass 123!");
        typeConfirm("Pass 123!");
        driver.findElement(By.id("email")).click();

        Thread.sleep(3000);

        assertPasswordErrorVisible();
        assertSignUpButtonDisabled();
    }

    @Test
    @DisplayName("Confirm password mismatch → confirm error")
    void shouldShowErrorForPasswordMismatch() throws InterruptedException {
        typeEmail("valid@email.com");
        typeUsername("ValidUsername");
        typePassword("ValidPass123!");
        typeConfirm("DifferentPass456!");
        driver.findElement(By.id("email")).click();

        Thread.sleep(3000);

        assertConfirmErrorVisible();
        assertSignUpButtonDisabled();
    }

    private void typeEmail(String value) {
        WebElement field = driver.findElement(By.id("email"));
        field.clear();
        field.sendKeys(value);
    }

    private void typeUsername(String value) {
        WebElement field = driver.findElement(By.id("firstName"));
        field.clear();
        field.sendKeys(value);
    }

    private void typePassword(String value) {
        WebElement field = driver.findElement(By.id("password"));
        field.clear();
        field.sendKeys(value);
    }

    private void typeConfirm(String value) {
        WebElement field = driver.findElement(By.id("repeatPassword"));
        field.clear();
        field.sendKeys(value);
    }

    private void clickSignUp() {
        driver.findElement(By.cssSelector("button[type='submit'].greenStyle")).click();
    }

    private void assertEmailErrorVisible() {
        WebElement error = driver.findElement(By.id("email-err-msg"));
        assertTrue(error.isDisplayed(), "Email error message should be visible");
        String text = error.getText().toLowerCase();
        assertTrue(text.contains("check") || text.contains("correctly") || text.contains("required"),
                "Email error text was: " + error.getText());
    }

    private void assertUsernameErrorVisible() {
        WebElement error = driver.findElement(By.id("firstname-err-msg"));
        assertTrue(error.isDisplayed(), "Username error message should be visible");
    }

    private void assertPasswordErrorVisible() {
        WebElement container = driver.findElement(By.cssSelector("div.main-data-input-password.wrong-input"));
        assertTrue(container.isDisplayed(), "Password input should have 'wrong-input' class indicating a validation error");
    }

    private void assertConfirmErrorVisible() {
        WebElement error = driver.findElement(By.id("confirm-err-msg"));
        assertTrue(error.isDisplayed(), "Confirm password error message should be visible");
    }

    private void assertSignUpButtonDisabled() {
        WebElement btn = driver.findElement(By.cssSelector("button[type='submit'].greenStyle"));
        assertFalse(btn.isEnabled(), "The 'Sign Up' button should be disabled with invalid data");
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
