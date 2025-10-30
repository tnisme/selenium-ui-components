package com.seleniumui.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class JsExecutor {

    private JsExecutor() {}

    public static void scrollToElement(WebDriver driver, By locator) {
        if (!isElementInViewport(driver, locator) || isCoveredByAnotherElement(driver, locator)) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({behavior: 'auto', block: 'center', inline: 'center'});", driver.findElement(locator));
            System.out.println("Scrolled to element: " + locator.toString());
        }
    }

    public static void scrollToElement(WebDriver driver, WebElement element) {
        if (!isElementInViewport(driver, element) || isCoveredByAnotherElement(driver, element)) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({behavior: 'auto', block: 'center', inline: 'center'});", element);
            System.out.println("Scrolled to element.");
        }
    }

    public static boolean isElementInViewport(WebDriver driver, By locator) {
        return isElementInViewport(driver, driver.findElement(locator));
    }

    public static boolean isElementInViewport(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        boolean isInViewport = (Boolean) js.executeScript(
                "const rect = arguments[0].getBoundingClientRect();" +
                        "return (" +
                        "rect.top >= 0 && " +
                        "rect.left >= 0 && " +
                        "rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) && " +
                        "rect.right <= (window.innerWidth || document.documentElement.clientWidth)" +
                        ");",
                element
        );
        System.out.println("Element is in viewport: " + isInViewport);
        return isInViewport;
    }

    public static boolean isCoveredByAnotherElement(WebDriver driver, By locator) {
        return isCoveredByAnotherElement(driver, driver.findElement(locator));
    }

    public static boolean isCoveredByAnotherElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        boolean isCovered = (Boolean) js.executeScript(
                "const elem = arguments[0];" +
                        "const rect = elem.getBoundingClientRect();" +
                        "const cx = rect.left + rect.width / 2;" +
                        "const cy = rect.top + rect.height / 2;" +
                        "const topElem = document.elementFromPoint(cx, cy);" +
                        "return !(elem === topElem || elem.contains(topElem));",
                element
        );
        System.out.println("Element is covered by another element: " + isCovered);
        return isCovered;
    }

    public static boolean isGlobalOverlayPresent(WebDriver driver) {
        String script = """
            const cx = window.innerWidth / 2;
            const cy = window.innerHeight / 2;
            const topElem = document.elementFromPoint(cx, cy);
            if (!topElem) return false;

            const overlayLike = /(overlay|modal|popup|backdrop|loading|spinner)/i.test(topElem.className);
            if (overlayLike) return true;

            const rect = topElem.getBoundingClientRect();
            const style = window.getComputedStyle(topElem);
            const coversScreen = rect.top <= 1 && rect.left <= 1 &&
                                 rect.bottom >= window.innerHeight - 1 &&
                                 rect.right >= window.innerWidth - 1;

            const blocksClick = style.pointerEvents !== 'none' && style.opacity > 0.1;

            const overflowHidden = document.body.style.overflow === 'hidden' ||
                                   document.documentElement.style.overflow === 'hidden';

            return (coversScreen && blocksClick) || overlayLike || overflowHidden;
        """;
        boolean isPresent = (Boolean) ((JavascriptExecutor) driver).executeScript(script);
        System.out.println("Global overlay present: " + isPresent);
        return isPresent;
    }

}
