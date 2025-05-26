const FAQManager = {
  init() {
    this.questions = document.querySelectorAll(".faq-question");
    if (!this.questions.length) {
      return;
    }
    this.setupQuestions();
  },

  setupQuestions() {
    this.questions.forEach((question) => {
      question.setAttribute("aria-expanded", "false");
      const answerId = question.getAttribute("aria-controls");
      const answer = document.getElementById(answerId);

      if (answer) {
        answer.setAttribute("aria-hidden", "true");
        answer.style.height = "0px";
        const answerContent = answer.querySelector(".faq-answer-content");
        if (answerContent) {
          answerContent.style.opacity = "0";
        }
      }

      question.addEventListener("click", () => this.toggleQuestion(question));
    });
  },

  toggleQuestion(question) {
    const isExpanding = question.getAttribute("aria-expanded") === "false";
    const answerId = question.getAttribute("aria-controls");
    const answer = document.getElementById(answerId);
    const answerContent = answer
      ? answer.querySelector(".faq-answer-content")
      : null;

    if (!answer || !answerContent) return;

    if (isExpanding) {
      this.questions.forEach((otherQuestion) => {
        if (
          otherQuestion !== question &&
          otherQuestion.getAttribute("aria-expanded") === "true"
        ) {
          this.collapseAnswer(otherQuestion);
        }
      });
    }

    question.setAttribute("aria-expanded", String(isExpanding));
    answer.setAttribute("aria-hidden", String(!isExpanding));

    if (isExpanding) {
      answer.style.height = answer.scrollHeight + "px";
      setTimeout(() => {
        answerContent.style.opacity = "1";
      }, 50);
    } else {
      answerContent.style.opacity = "0";
      answer.style.height = "0px";
    }
  },

  collapseAnswer(question) {
    question.setAttribute("aria-expanded", "false");
    const answerId = question.getAttribute("aria-controls");
    const answer = document.getElementById(answerId);
    const answerContent = answer
      ? answer.querySelector(".faq-answer-content")
      : null;

    if (!answer || !answerContent) return;

    answer.setAttribute("aria-hidden", "true");
    answerContent.style.opacity = "0";
    answer.style.height = "0px";
  },
};

export default FAQManager;
