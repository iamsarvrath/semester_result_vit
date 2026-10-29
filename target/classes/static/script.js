const form = document.querySelector("#result-form");
const resultSection = document.querySelector("#result");
const resetButton = document.querySelector("#reset-button");

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  const rows = [...document.querySelectorAll("tbody tr")];
  const subjects = rows.map((row) => {
    const inputs = row.querySelectorAll("input");
    const mse = Number(inputs[1].value);
    const ese = Number(inputs[2].value);
    const credits = Number(inputs[3].value);
    return { name: inputs[0].value || "Subject", mse, ese, credits };
  });
  try {
    const response = await fetch("api/results", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        studentName: document.querySelector("#student-name").value,
        prn: document.querySelector("#student-prn").value,
        subjects
      })
    });
    if (!response.ok) throw new Error("The result could not be saved.");
    const result = await response.json();
    document.querySelector("#percentage").textContent = `${result.percentage.toFixed(2)}%`;
    document.querySelector("#sgpa").textContent = result.sgpa.toFixed(2);
    document.querySelector("#grade").textContent = result.grade;
    document.querySelector("#status").textContent = result.status;
    document.querySelector("#result-title").textContent = `${result.studentName}'s result`;
    document.querySelector("#subject-results").innerHTML = result.subjects.map((subject) =>
      `<div class="subject-result"><span>${subject.name}</span><strong>${(subject.mse + subject.ese).toFixed(1)} / 100</strong></div>`
    ).join("");
    resultSection.scrollIntoView({ behavior: "smooth", block: "start" });
  } catch (error) {
    document.querySelector("#status").textContent = error.message + " Start Spring Boot and check MySQL.";
  }
});

resetButton.addEventListener("click", () => {
  form.reset();
  document.querySelector("#percentage").textContent = "—";
  document.querySelector("#sgpa").textContent = "—";
  document.querySelector("#grade").textContent = "—";
  document.querySelector("#status").textContent = "Complete the form to view";
  document.querySelector("#result-title").textContent = "Result summary";
  document.querySelector("#subject-results").innerHTML = "";
});
