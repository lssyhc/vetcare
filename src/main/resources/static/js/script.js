import MobileMenu from "./mobileMenu.js";
import FAQManager from "./faqManager.js";

document.addEventListener("DOMContentLoaded", () => {
  new MobileMenu();
  FAQManager.init();
});
