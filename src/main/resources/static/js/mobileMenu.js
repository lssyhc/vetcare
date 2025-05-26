class MobileMenu {
  constructor() {
    this.button = document.getElementById("mobile-menu-button");
    this.menu = document.getElementById("mobile-menu");
    this.iconHamburger = document.getElementById("icon-hamburger");
    this.iconClose = document.getElementById("icon-close");

    if (this.button && this.menu && this.iconHamburger && this.iconClose) {
      this.init();
    }
  }

  init() {
    this.handleButtonClick = this.handleButtonClick.bind(this);
    this.handleClickOutside = this.handleClickOutside.bind(this);
    this.handleNavClick = this.handleNavClick.bind(this);
    this.setupEventListeners();
  }

  handleButtonClick() {
    const isCurrentlyHiddenOrClosing =
      !this.menu.classList.contains("menu-open");
    this.setMenuState(isCurrentlyHiddenOrClosing);
  }

  handleClickOutside(e) {
    if (!this.button.contains(e.target) && !this.menu.contains(e.target)) {
      if (this.menu.classList.contains("menu-open")) {
        this.setMenuState(false);
      }
    }
  }

  handleNavClick(e) {
    const clickedLink = e.target.closest("a");
    if (clickedLink && this.menu.contains(clickedLink)) {
      if (clickedLink.getAttribute("href").startsWith("#")) {
        e.preventDefault();
        const targetId = clickedLink.getAttribute("href").slice(1);
        const targetElement = document.getElementById(targetId);
        this.setMenuState(false);
        if (targetElement) {
          targetElement.scrollIntoView({
            behavior: "smooth",
            block: "start",
          });
        }
      } else {
        this.setMenuState(false);
      }
    }
  }

  setMenuState(shouldBeOpen) {
    if (shouldBeOpen) {
      this.menu.classList.remove("invisible");
      requestAnimationFrame(() => {
        this.menu.classList.add("menu-open");
      });
    } else {
      this.menu.classList.remove("menu-open");
      setTimeout(() => {
        if (!this.menu.classList.contains("menu-open")) {
          this.menu.classList.add("invisible");
        }
      }, 300);
    }

    document.body.classList.toggle("overflow-hidden", shouldBeOpen);
    if (this.iconHamburger && this.iconClose) {
      this.iconHamburger.classList.toggle("hidden", shouldBeOpen);
      this.iconClose.classList.toggle("hidden", !shouldBeOpen);
    }
  }

  setupEventListeners() {
    this.button.addEventListener("click", this.handleButtonClick);
    document.addEventListener("click", this.handleClickOutside);
    this.menu.addEventListener("click", this.handleNavClick);
  }
}

export default MobileMenu;
