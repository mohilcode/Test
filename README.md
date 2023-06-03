# ShopSmartScan

ShopSmartScan is an innovative Android app aimed at helping foreigners in Japan understand product details easily. This app lets users scan the barcode of any item and presents the product's information in the English language. ShopSmartScan is developed using Kotlin and has been designed with the primary aim of breaking the language barrier for foreigners when shopping in Japan.

This app currently supports a variety of products, leveraging various e-commerce platforms like Rakuten, Amazon, Yahoo Shopping, etc., to pull relevant product information. In the future, we aim to add more websites and broaden the product information database. 

## Demo

Check out this practical demo of how ShopSmartScan can help a non-Japanese speaker to understand the details of a medicine product in a local pharmacy:

**Scenario:** My friend caught a cold and needed to find a suitable remedy. He went to the local pharmacy, Matsumoto Kiyoshi, to purchase a cold medicine. However, he faced a significant barrier - every product was labeled in Japanese, and he couldn't identify which one was for cold relief.

This is when ShopSmartScan came to the rescue. By simply scanning the barcode of a product he assumed might be cold-related, the app instantly provided him with a detailed English description. This enabled him to make an informed decision, confirming that the medicine was indeed for cold relief.


https://github.com/mohilcode/shopsmartscan/assets/112541468/318a0142-97a1-4374-a739-367ac737a018


## Live App

To request access to the APK, please visit [here](https://forms.gle/7SBBhRYQHER1F5wS6).

## Features

- **Barcode Scanner:** Integrated barcode scanner to scan and extract the GTIN of products.
- **Image Verification:** Using Google's Custom Search API, the app presents images of the searched product for user verification.
- **Product Information Extraction:** The extracted GTIN is used to search product information on various e-commerce websites.
- **Translation:** The product name and information extracted are translated into English using the GPT API.
- **In-app Webpage Access:** If a user feels the displayed information does not match their product, they can access the webpage (from where the image was sourced) directly within the app. This page opens with automatic English translation via Chrome's translate feature.

## Future Goals

- **Launching on Google Play Store:** As a part of my future goals, I am planning to launch the ShopSmartScan app on the Google Play Store. This would make it more accessible and convenient for users to install and update the app.
- **UI/UX Improvements:** Ongoing improvements to the user interface and user experience to make the product recognition process seamless.
- **More Websites:** Planning to include more websites for wider product information coverage.
- **Ingredient Recognition:** Looking to introduce an ingredient listing feature for food items, assisting vegetarians/vegans in their food choices.
- **Multi-Language Support:** Though currently supporting only English, we plan to include more languages in the future.

## Support

If you find this project valuable, please give it a ⭐ on GitHub!

For any queries or suggestions, feel free to open an issue on GitHub or connect with me directly.

With ShopSmartScan, let's make shopping in Japan hassle-free for everyone!
