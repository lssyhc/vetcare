class SidebarToggle {
  constructor() {
    this.mobileMenuButton = null;
    this.sidebar = null;
    this.sidebarOverlay = null;
    this.body = document.body;
    this.isInitializing = true;
    this.hasUserInteracted = false;

    this.init();
  }

  init() {
    if (document.readyState === "loading") {
      document.addEventListener("DOMContentLoaded", () => this.setup());
    } else {
      this.setup();
    }
  }

  setup() {
    this.mobileMenuButton = document.getElementById("mobile-menu-button");
    this.sidebar = document.getElementById("sidebar");
    this.sidebarOverlay = document.getElementById("sidebar-overlay");

    this.bindEvents();
    this.initializeState();
    this.bindSidebarNavigation();

    setTimeout(() => {
      this.isInitializing = false;
    }, 100);
  }

  bindEvents() {
    if (this.mobileMenuButton) {
      this.mobileMenuButton.addEventListener("click", () => {
        this.hasUserInteracted = true;
        this.toggleSidebar();
      });
    }

    if (this.sidebarOverlay) {
      this.sidebarOverlay.addEventListener("click", () => {
        this.hasUserInteracted = true;
        this.toggleSidebar();
      });
    }

    window.addEventListener("resize", () => {
      if (window.innerWidth >= 1024) {
        if (this.sidebar) {
          this.sidebar.classList.remove("-translate-x-full");
        }
        if (this.sidebarOverlay) {
          this.sidebarOverlay.classList.add("hidden");
        }
        this.body.classList.remove("dashboard-sidebar-closed");
        this.body.classList.add("dashboard-sidebar-open");
      } else {
        if (
          this.sidebar &&
          !this.sidebar.classList.contains("-translate-x-full")
        ) {
          this.hasUserInteracted = true;
          this.closeSidebar();
        }
      }
    });
  }

  bindSidebarNavigation() {
    if (this.sidebar) {
      const sidebarLinks = this.sidebar.querySelectorAll(".sidebar-nav-link");
      sidebarLinks.forEach((link) => {
        link.addEventListener("click", () => {
          if (window.innerWidth < 1024) {
            this.hasUserInteracted = true;
            this.closeSidebar();
          }
        });
      });
    }
  }

  initializeState() {
    if (this.sidebar) {
      this.sidebar.classList.add("initializing");
    }

    if (window.innerWidth >= 1024) {
      this.body.classList.add("dashboard-sidebar-open");
      if (this.sidebarOverlay) {
        this.sidebarOverlay.classList.add("hidden");
      }
    } else {
      this.body.classList.add("dashboard-sidebar-closed");
      if (this.sidebar) {
        this.sidebar.classList.add("-translate-x-full");
      }
      if (this.sidebarOverlay) {
        this.sidebarOverlay.classList.add("hidden");
      }
    }

    setTimeout(() => {
      if (this.sidebar) {
        this.sidebar.classList.remove("initializing");
      }
    }, 100);
  }

  toggleSidebar() {
    if (!this.sidebar || this.isInitializing) return;

    const isOpen = !this.sidebar.classList.contains("-translate-x-full");

    this.sidebar.classList.toggle("-translate-x-full");
    if (this.sidebarOverlay) {
      this.sidebarOverlay.classList.toggle("hidden");
    }

    if (isOpen) {
      this.body.classList.remove("dashboard-sidebar-open");
      this.body.classList.add("dashboard-sidebar-closed");
    } else {
      this.body.classList.remove("dashboard-sidebar-closed");
      this.body.classList.add("dashboard-sidebar-open");
    }
  }

  closeSidebar() {
    if (!this.sidebar || this.isInitializing) return;

    this.sidebar.classList.add("-translate-x-full");
    if (this.sidebarOverlay) {
      this.sidebarOverlay.classList.add("hidden");
    }
    this.body.classList.remove("dashboard-sidebar-open");
    this.body.classList.add("dashboard-sidebar-closed");
  }

  openSidebar() {
    if (!this.sidebar || this.isInitializing) return;

    this.sidebar.classList.remove("-translate-x-full");
    if (this.sidebarOverlay) {
      this.sidebarOverlay.classList.remove("hidden");
    }
    this.body.classList.remove("dashboard-sidebar-closed");
    this.body.classList.add("dashboard-sidebar-open");
  }

  isSidebarOpen() {
    return (
      this.sidebar && !this.sidebar.classList.contains("-translate-x-full")
    );
  }
}

const sidebarToggle = new SidebarToggle();
