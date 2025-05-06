# 🛠️ GitLab Pipeline Execution Guide for Performance Testing

This document explains how to execute the **Web Performance Test** and **WS Performance Test** pipelines, configure the required variables, and receive test results via email.

---

## 📌 Pipeline Overview

### 1. **Web Performance Test**
This pipeline tests the performance of web applications.

### 2. **WS Performance Test**
This pipeline tests the performance of web services (APIs).

---

## ▶️ How to Run the Pipelines

### 1. Access the Pipelines
1. Open the GitLab project.
2. Navigate to **CI/CD > Pipeline Schedules**.

### 2. Run the Desired Pipeline
1. Locate the pipeline you want to run:
    - **Web Performance Test**
    - **WS Performance Test**
2. Click the **Play** button on the right of the pipeline to trigger it manually.

---

## ⚙️ Variable Configuration

### Viewing and Editing Variables
1. Go to **CI/CD > Pipeline Schedules**.
2. Click the **Edit (pencil icon)** next to the desired pipeline.
3. Modify the variables according to your testing needs.

---

### Variables per Pipeline

#### Web Performance Test

| Variable Name  | Description                          | Example Value                                                                                  |
|----------------|--------------------------------------|------------------------------------------------------------------------------------------------|
| `SITE`         | Name of the site under test          | `La Corniche de la Plage`                                                                      |
| `URL`          | Web page URL                         | `https://polo.com`         |
| `MAIL_LIST`    | Comma-separated list of emails       | `mail_list`                      |
| `DURATION`     | Test duration in seconds             | `3600`                                                                                         |
| `DATEDEBUT`    | Start date of the test               | `01/06/2024`                                                                                   |
| `DATEFIN`      | End date of the test                 | `15/06/2024`                                                                                   |
| `COMMENTAIRE`  | Comments for the test                | `test perf 30-10-24`                                                                           |
| `ENV`          | Environment tag                      | `A5`                                                                                           |

---

#### WS Performance Test

| Variable Name  | Description                          | Example Value                                                                 |
|----------------|--------------------------------------|------------------------------------------------------------------------------|
| `SITE`         | Name of the site under test          | `BTP`                                                                        |
| `HEBERGEMENT`  | Hosting environment name             | `BTP24X`                                                                     |
| `DATE`         | Date for the test                    | `2025-02-25`                                                                 |
| `ASSURANCE`    | Type of insurance                    | `XXXACPACKDY`                                                                |
| `PRESTATION`   | Type of service                      | `BTPANIMAL`                                                                  |
| `URL`          | API endpoint                         | `wsdl url`        |
| `MAIL_LIST`    | Comma-separated list of emails       | `mail_list`    |
| `DURATION`     | Test duration in seconds             | `3600`                                                                       |
| `ENV`          | Environment tag                      | `A5`                                                                         |

---

## ✏️ How to Customize Variables

### Example: Web Pipeline

To test a different web environment:

- Update `URL` with the target environment's URL.
- Set `ENV` to the appropriate value.
- Adjust `DURATION` as needed.
- Add the recipients in `MAIL_LIST`.

```yaml
URL=https://test.example.com
ENV=T4
DURATION=3600
MAIL_LIST=tester@example.com
