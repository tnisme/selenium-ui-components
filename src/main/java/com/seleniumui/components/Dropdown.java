package com.seleniumui.components;

import com.seleniumui.core.actions.SmartActions;
import com.seleniumui.core.BaseComponent;
import com.seleniumui.core.waits.SmartWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Dropdown extends BaseComponent {

    public Dropdown(WebDriver driver, By locator) {
        super(Objects.requireNonNull(driver, "WebDriver cannot be null"),
                Objects.requireNonNull(locator, "Locator cannot be null"));
    }

    public void selectByText(String text) {
        Objects.requireNonNull(text, "Selection text cannot be null");

        WebElement dropdown = SmartWait.forVisible(driver, locator);

        if (isStandardSelect(dropdown)) {
            selectStandardByText(dropdown, text);
        } else {
            selectCustomByText(dropdown, text);
        }
    }

    public void selectByValue(String value) {
        Objects.requireNonNull(value, "Selection value cannot be null");

        WebElement dropdown = SmartWait.forVisible(driver, locator);

        if (isStandardSelect(dropdown)) {
            Select select = new Select(dropdown);
            select.selectByValue(value);
        } else {
            selectCustomByValue(dropdown, value);
        }
    }

    public void selectByTexts(List<String> texts) {
        Objects.requireNonNull(texts, "Selection texts cannot be null");

        WebElement dropdown = SmartWait.forVisible(driver, locator);

        if (isStandardSelect(dropdown)) {
            Select select = new Select(dropdown);
            if (!select.isMultiple()) {
                throw new IllegalStateException("Standard dropdown is not multiselect");
            }
            select.deselectAll();
            for (String text : texts) {
                select.selectByVisibleText(text);
            }
        } else {
            selectCustomByTexts(dropdown, texts);
        }
    }

    public void selectByValues(List<String> values) {
        Objects.requireNonNull(values, "Selection values cannot be null");

        WebElement dropdown = SmartWait.forVisible(driver, locator);

        if (isStandardSelect(dropdown)) {
            Select select = new Select(dropdown);
            if (!select.isMultiple()) {
                throw new IllegalStateException("Standard dropdown is not multiselect");
            }
            select.deselectAll();
            for (String value : values) {
                select.selectByValue(value);
            }
        } else {
            selectCustomByValues(dropdown, values);
        }
    }

    public void addSelectionByText(String text) {
        Objects.requireNonNull(text, "Selection text cannot be null");

        WebElement dropdown = SmartWait.forVisible(driver, locator);

        if (isStandardSelect(dropdown)) {
            Select select = new Select(dropdown);
            if (!select.isMultiple()) {
                throw new IllegalStateException("Cannot add selection to single select dropdown");
            }
            select.selectByVisibleText(text);
        } else {
            addCustomSelectionByText(dropdown, text);
        }
    }

    public void deselectByText(String text) {
        Objects.requireNonNull(text, "Deselection text cannot be null");

        WebElement dropdown = SmartWait.forVisible(driver, locator);

        if (isStandardSelect(dropdown)) {
            Select select = new Select(dropdown);
            if (!select.isMultiple()) {
                throw new IllegalStateException("Cannot deselect from single select dropdown");
            }
            select.deselectByVisibleText(text);
        } else {
            deselectCustomByText(dropdown, text);
        }
    }

    public void deselectAll() {
        WebElement dropdown = SmartWait.forVisible(driver, locator);

        if (isStandardSelect(dropdown)) {
            Select select = new Select(dropdown);
            if (!select.isMultiple()) {
                throw new IllegalStateException("Cannot deselect all from single select dropdown");
            }
            select.deselectAll();
        } else {
            deselectAllCustom(dropdown);
        }
    }

    public String getSelectedText() {
        WebElement dropdown = SmartWait.forVisible(driver, locator);

        if (isStandardSelect(dropdown)) {
            Select select = new Select(dropdown);
            if (select.isMultiple()) {
                return select.getAllSelectedOptions().stream()
                        .findFirst()
                        .map(WebElement::getText)
                        .map(String::trim)
                        .orElse("");
            }
            return select.getFirstSelectedOption().getText().trim();
        } else {
            return getCustomSelectedText(dropdown);
        }
    }

    public List<String> getSelectedTexts() {
        WebElement dropdown = SmartWait.forVisible(driver, locator);

        if (isStandardSelect(dropdown)) {
            Select select = new Select(dropdown);
            return select.getAllSelectedOptions().stream()
                    .map(WebElement::getText)
                    .map(String::trim)
                    .collect(Collectors.toList());
        } else {
            return getCustomSelectedTexts(dropdown);
        }
    }

    public String getSelectedValue() {
        WebElement dropdown = SmartWait.forVisible(driver, locator);

        if (isStandardSelect(dropdown)) {
            Select select = new Select(dropdown);
            if (select.isMultiple()) {
                return select.getAllSelectedOptions().stream()
                        .findFirst()
                        .map(element -> element.getAttribute("value"))
                        .orElse("");
            }
            return select.getFirstSelectedOption().getAttribute("value");
        } else {
            return getCustomSelectedValue(dropdown);
        }
    }

    public List<String> getSelectedValues() {
        WebElement dropdown = SmartWait.forVisible(driver, locator);

        if (isStandardSelect(dropdown)) {
            Select select = new Select(dropdown);
            return select.getAllSelectedOptions().stream()
                    .map(element -> element.getAttribute("value"))
                    .collect(Collectors.toList());
        } else {
            return getCustomSelectedValues(dropdown);
        }
    }

    public boolean isMultiple() {
        WebElement dropdown = SmartWait.forVisible(driver, locator);

        if (isStandardSelect(dropdown)) {
            return new Select(dropdown).isMultiple();
        } else {
            return isCustomMultiple(dropdown);
        }
    }

    private boolean isStandardSelect(WebElement dropdown) {
        return "select".equalsIgnoreCase(dropdown.getTagName());
    }

    private void selectStandardByText(WebElement dropdown, String text) {
        try {
            Select select = new Select(dropdown);
            select.selectByVisibleText(text);
        } catch (Exception e) {
            try {
                WebElement option = dropdown.findElement(
                        By.xpath(".//option[normalize-space()='" + text + "']"));
                SmartWait.forClickable(driver, option);
                SmartActions.click(option);
            } catch (Exception ex) {
                throw new RuntimeException("Cannot select option with text: " + text, ex);
            }
        }
    }

    private void selectCustomByText(WebElement dropdown, String text) {
        SmartWait.forClickable(driver, dropdown);
        SmartActions.click(dropdown);
        WebElement option = findCustomOption(text);
        SmartWait.forClickable(driver, option);
        SmartActions.click(option);
    }

    private void selectCustomByTexts(WebElement dropdown, List<String> texts) {
        SmartWait.forClickable(driver, dropdown);
        SmartActions.click(dropdown);

        for (String text : texts) {
            WebElement option = findCustomOption(text);
            selectCustomOption(option);
        }

        closeCustomDropdown(dropdown);
    }

    private void addCustomSelectionByText(WebElement dropdown, String text) {
        if (!isCustomDropdownOpen(dropdown)) {
            SmartWait.forClickable(driver, dropdown);
            SmartActions.click(dropdown);
        }

        WebElement option = findCustomOption(text);
        selectCustomOption(option);
    }

    private void selectCustomByValue(WebElement dropdown, String value) {
        SmartWait.forClickable(driver, dropdown);
        SmartActions.click(dropdown);
        WebElement option = findOptionByValue(value);
        SmartWait.forClickable(driver, option);
        SmartActions.click(option);
    }

    private void selectCustomByValues(WebElement dropdown, List<String> values) {
        SmartWait.forClickable(driver, dropdown);
        SmartActions.click(dropdown);

        for (String value : values) {
            WebElement option = findOptionByValue(value);
            selectCustomOption(option);
        }

        closeCustomDropdown(dropdown);
    }

    private void deselectCustomByText(WebElement dropdown, String text) {
        try {
            WebElement multiValueElement = dropdown.findElement(
                    By.xpath(".//*[contains(@class, 'multiValue') and contains(., '" + text + "')]"));

            WebElement removeButton = multiValueElement.findElement(
                    By.xpath(".//*[local-name()='svg'] | " +
                            ".//*[contains(@class, 'remove')] | " +
                            ".//*[contains(@class, 'clear')]"));
            SmartWait.forClickable(driver, removeButton);
            SmartActions.click(removeButton);
            return;

        } catch (Exception e) {
            // Fallback: mở dropdown và deselect option
        }

        if (!isCustomDropdownOpen(dropdown)) {
            SmartWait.forClickable(driver, dropdown);
            SmartActions.click(dropdown);
        }

        WebElement option = findCustomOption(text);
        deselectCustomOption(option);
    }

    private void deselectAllCustom(WebElement dropdown) {
        try {
            List<WebElement> removeButtons = dropdown.findElements(
                    By.xpath(".//*[contains(@class, 'multiValue')]//*[local-name()='svg']"));

            for (WebElement removeButton : removeButtons) {
                SmartWait.forClickable(driver, removeButton);
                SmartActions.click(removeButton);
            }
        } catch (Exception e) {
            List<WebElement> selectedOptions = findCustomSelectedOptions(dropdown);
            for (WebElement option : selectedOptions) {
                deselectCustomOption(option);
            }
        }
    }

    private WebElement findCustomOption(String text) {
        WebElement dropdown = SmartWait.forVisible(driver, locator);

        By[] optionLocators = {
                By.xpath(".//*[normalize-space()='" + text + "' and not(ancestor::*[contains(@style,'display: none')])]"),
                By.xpath(".//option[normalize-space()='" + text + "']"),
                By.xpath(".//li[normalize-space()='" + text + "']"),
                By.xpath(".//div[normalize-space()='" + text + "']"),
                By.xpath(".//span[normalize-space()='" + text + "']"),
                By.xpath(".//a[normalize-space()='" + text + "']"),
                By.xpath(".//*[contains(normalize-space(), '" + text + "') and not(ancestor::*[contains(@style,'display: none')])]"),
                By.xpath(".//*[@data-value='" + text + "']"),
                By.xpath(".//*[@data-text='" + text + "']")
        };

        for (By locator : optionLocators) {
            try {
                WebElement option = dropdown.findElement(locator);
                if (option.isDisplayed()) {
                    return option;
                }
            } catch (Exception e) {
                // Continue to next strategy
            }
        }

        for (By locator : optionLocators) {
            try {
                String xpath = locator.toString().replace("By.xpath: .", "");
                WebElement option = driver.findElement(By.xpath(xpath));
                if (option.isDisplayed()) {
                    return option;
                }
            } catch (Exception e) {
                // Continue to next strategy
            }
        }

        throw new RuntimeException("Cannot find option with text: " + text);
    }

    private WebElement findOptionByValue(String value) {
        WebElement dropdown = SmartWait.forVisible(driver, locator);

        By[] valueLocators = {
                By.xpath(".//*[@value='" + value + "']"),
                By.xpath(".//*[@data-value='" + value + "']"),
                By.xpath(".//option[@value='" + value + "']"),
                By.xpath(".//li[@data-value='" + value + "']"),
                By.xpath(".//div[@data-value='" + value + "']")
        };

        for (By locator : valueLocators) {
            try {
                WebElement option = dropdown.findElement(locator);
                if (option.isDisplayed()) {
                    return option;
                }
            } catch (Exception e) {
                // Continue to next strategy
            }
        }

        for (By locator : valueLocators) {
            try {
                String xpath = locator.toString().replace("By.xpath: .", "");
                WebElement option = driver.findElement(By.xpath(xpath));
                if (option.isDisplayed()) {
                    return option;
                }
            } catch (Exception e) {
                // Continue to next strategy
            }
        }

        throw new RuntimeException("Cannot find option with value: " + value);
    }

    private String getCustomSelectedText(WebElement dropdown) {
        List<String> selectedTexts = getCustomSelectedTexts(dropdown);
        if (!selectedTexts.isEmpty()) {
            return selectedTexts.get(0);
        }

        String[] selectedAttributes = {
                ".//*[@aria-selected='true']",
                ".//*[contains(@class, 'selected') and not(contains(@class, 'option'))]",
                ".//*[contains(@class, 'selection')]",
                ".//*[contains(@class, 'selected-text')]",
                ".//*[contains(@class, 'current')]"
        };

        for (String selector : selectedAttributes) {
            try {
                List<WebElement> elements = dropdown.findElements(By.xpath(selector));
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        String text = element.getText().trim();
                        if (isValidSelectedText(text)) {
                            return text;
                        }
                    }
                }
            } catch (Exception e) {
                // Continue
            }
        }

        return dropdown.getText().trim();
    }

    private List<String> getCustomSelectedTexts(WebElement dropdown) {
        List<String> selectedTexts = new ArrayList<>();

        try {
            List<WebElement> multiValueElements = dropdown.findElements(
                    By.xpath(".//*[contains(@class, 'multiValue')]//*[text()] | " +
                            ".//*[contains(@class, 'multiValue')]//*[contains(@class, 'value')] | " +
                            ".//*[contains(@class, 'selected') and not(contains(@class, 'option'))]"
                    ));

            for (WebElement element : multiValueElements) {
                if (element.isDisplayed()) {
                    String text = element.getText().trim();
                    if (!text.isEmpty() && isValidSelectedText(text)) {
                        selectedTexts.add(text);
                    }
                }
            }

            if (!selectedTexts.isEmpty()) {
                return selectedTexts;
            }
        } catch (Exception e) {
            // Continue to next strategy
        }

        try {
            String dropdownText = dropdown.getText().trim();
            if (!dropdownText.isEmpty()) {
                List<String> extractedTexts = extractSelectedTextsFromDropdown(dropdownText);
                if (!extractedTexts.isEmpty()) {
                    return extractedTexts;
                }
            }
        } catch (Exception e) {
            // Continue to next strategy
        }

        List<WebElement> visibleElements = dropdown.findElements(
                By.xpath(".//*[not(contains(@style,'display:none')) " +
                        "and string-length(normalize-space(text())) > 0 " +
                        "and not(contains(@class, 'option')) " +
                        "and not(contains(@class, 'select'))]"));

        for (WebElement element : visibleElements) {
            try {
                if (element.isDisplayed()) {
                    String text = element.getText().trim();
                    if (isValidMultiselectText(text)) {
                        selectedTexts.add(text);
                    }
                }
            } catch (Exception e) {
                // Skip this element
            }
        }

        return selectedTexts;
    }

    private List<String> extractSelectedTextsFromDropdown(String dropdownText) {
        List<String> texts = new ArrayList<>();

        if (!dropdownText.isEmpty()) {
            String[] parts = dropdownText.split("[,|\\n|\\r|\\t]+");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty() && isValidMultiselectText(trimmed)) {
                    texts.add(trimmed);
                }
            }
        }

        return texts;
    }

    private List<WebElement> findCustomSelectedOptions(WebElement dropdown) {
        By[] selectedOptionLocators = {
                By.xpath(".//*[contains(@class, 'selected')]"),
                By.xpath(".//*[@aria-selected='true']"),
                By.xpath(".//*[contains(@class, 'active')]"),
                By.xpath(".//input[@type='checkbox' and @checked]/.."),
                By.xpath(".//input[@type='checkbox' and @checked]/preceding-sibling::*"),
                By.xpath(".//input[@type='checkbox' and @checked]/following-sibling::*")
        };

        List<WebElement> selectedOptions = new ArrayList<>();
        for (By locator : selectedOptionLocators) {
            try {
                List<WebElement> elements = dropdown.findElements(locator);
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        selectedOptions.add(element);
                    }
                }
            } catch (Exception e) {
                // Continue
            }
        }

        return selectedOptions;
    }

    private String getCustomSelectedValue(WebElement dropdown) {
        String[] valueAttributes = {"value", "data-value", "data-selected-value"};

        for (String attr : valueAttributes) {
            String value = dropdown.getAttribute(attr);
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }

        try {
            WebElement hiddenInput = dropdown.findElement(By.xpath(".//input[@type='hidden']"));
            String value = hiddenInput.getAttribute("value");
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        } catch (Exception e) {
            // Ignore
        }

        return "";
    }

    private List<String> getCustomSelectedValues(WebElement dropdown) {
        List<String> values = new ArrayList<>();
        List<WebElement> selectedOptions = findCustomSelectedOptions(dropdown);

        for (WebElement option : selectedOptions) {
            String value = option.getAttribute("value");
            if (value != null && !value.trim().isEmpty()) {
                values.add(value.trim());
            } else {
                String dataValue = option.getAttribute("data-value");
                if (dataValue != null && !dataValue.trim().isEmpty()) {
                    values.add(dataValue.trim());
                }
            }
        }

        return values;
    }

    private boolean isCustomMultiple(WebElement dropdown) {
        String multipleAttr = dropdown.getAttribute("multiple");
        String ariaMulti = dropdown.getAttribute("aria-multiselectable");
        String className = dropdown.getAttribute("class");

        return "true".equals(multipleAttr) ||
                "true".equals(ariaMulti) ||
                (className != null && className.contains("multiple"));
    }

    private boolean isCustomDropdownOpen(WebElement dropdown) {
        try {
            String expanded = dropdown.getAttribute("aria-expanded");
            if ("true".equals(expanded)) return true;

            String className = dropdown.getAttribute("class");
            if (className != null && (className.contains("open") || className.contains("expanded"))) {
                return true;
            }

            return dropdown.findElements(By.xpath(".//*[contains(@class, 'option')]"))
                    .stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }

    private void selectCustomOption(WebElement option) {
        try {
            WebElement checkbox = option.findElement(By.xpath(".//input[@type='checkbox']"));
            if (!checkbox.isSelected()) {
                SmartWait.forClickable(driver, checkbox);
                SmartActions.scrollToElement(driver, checkbox);
                SmartActions.click(checkbox);
            }
        } catch (Exception e) {
            SmartActions.click(option);
        }
    }

    private void deselectCustomOption(WebElement option) {
        try {
            WebElement checkbox = option.findElement(By.xpath(".//input[@type='checkbox']"));
            if (checkbox.isSelected()) {
                SmartWait.forClickable(driver, checkbox);
                SmartActions.click(checkbox);
            }
        } catch (Exception e) {
            SmartWait.forClickable(driver, option);
            SmartActions.click(option);
        }
    }

    private void closeCustomDropdown(WebElement dropdown) {
        try {
            SmartWait.forClickable(driver, dropdown);
            SmartActions.click(dropdown);
        } catch (Exception e) {
            // Ignore if it cannot close
        }
    }

    private boolean isValidSelectedText(String text) {
        if (text.isEmpty()) return false;

        String lowerText = text.toLowerCase();
        return !lowerText.contains("select") &&
                !lowerText.contains("option") &&
                !lowerText.contains("search") &&
                !lowerText.matches(".*\\d+ options.*") &&
                text.length() < 100;
    }

    private boolean isValidMultiselectText(String text) {
        if (text.isEmpty()) return false;

        String lowerText = text.toLowerCase();
        return !lowerText.contains("select") &&
                !lowerText.contains("option") &&
                !lowerText.contains("search") &&
                !lowerText.matches(".*\\d+ options.*") &&
                !lowerText.contains("click to") &&
                text.length() < 50;
    }
}
