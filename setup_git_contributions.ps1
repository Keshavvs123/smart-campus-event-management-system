$ErrorActionPreference = "Stop"

# Initialize Git
git init -b main

# Add remote
git remote add origin https://github.com/Keshavvs123/smart-campus-event-management-system.git

# Initial base commit authored by Keshav
git add .gitignore
git add pom.xml
git add src/main/resources/application.properties
git add src/main/java/com/smcem/SmcemApplication.java
git add src/main/resources/static/css
git add src/main/resources/templates/layout.html
git add src/main/resources/templates/login.html
git add src/main/resources/templates/register.html
git commit -m "Initialize SMCEM Platform Base Core" --author="Keshav Vithal Sangonda <Keshavvs123@users.noreply.github.com>"

# 1. Kasa Guruvi Reddy (Admin & Security Module)
git checkout -b feature/admin-security
git add src/main/java/com/smcem/config
git add src/main/java/com/smcem/controller/AdminController.java
git add src/main/java/com/smcem/service/UserService.java
git add src/main/resources/templates/admin
git commit -m "Setup Admin Dashboard & Core Security Infrastructure" --author="Kasa Guruvi Reddy <gurivireddy13@users.noreply.github.com>"
git checkout main

# 2. Jeevan A N (Faculty Review Module)
git checkout -b feature/faculty-review
git add src/main/java/com/smcem/controller/FacultyController.java
git add src/main/resources/templates/faculty
git commit -m "Implement Faculty Review Ecosystem & Feedback Channel" --author="Jeevan A N <jeevan-an-17@users.noreply.github.com>"
git checkout main

# 3. Pushkar P (Organizer & Event Module)
git checkout -b feature/organizer-events
git add src/main/java/com/smcem/controller/OrganizerController.java
git add src/main/java/com/smcem/service/EventService.java
git add src/main/java/com/smcem/service/AttendanceService.java
git add src/main/resources/templates/organizer
git commit -m "Develop Event Lifecycle & Active Attendance Engine" --author="Pushkar P <pushkar51718@users.noreply.github.com>"
git checkout main

# 4. Keshav Vithal Sangonda (Student Workflows & Final Wrap)
git checkout -b feature/student-workflows
# Add all remaining Java entities, repositories, and controllers
git add src/main/java/com/smcem/entity
git add src/main/java/com/smcem/repository
git add src/main/java/com/smcem/controller/StudentController.java
git add src/main/java/com/smcem/controller/HomeController.java
git add src/main/java/com/smcem/service/RegistrationService.java
git add src/main/resources/templates/student
git add src/main/resources/templates/about.html
git add src/main/resources/templates/contact.html
git add src/main/resources/templates/privacy.html
# Add any other remaining service files
git add src/main/java/com/smcem/service
git add src/main/java/com/smcem
git commit -m "Build Student Registration, Payment Gateway & Certificate Logic" --author="Keshav Vithal Sangonda <Keshavvs123@users.noreply.github.com>"
git checkout main

# Merge all branches back into main cleanly to create a graph
git merge feature/admin-security -m "Merge Admin & Security Module"
git merge feature/faculty-review -m "Merge Faculty Review Module"
git merge feature/organizer-events -m "Merge Organizer Module"
git merge feature/student-workflows -m "Merge Student Module"

# Final sanity check to add any missed files
git add .
git commit -m "Final Polish and Integration" --author="Keshav Vithal Sangonda <Keshavvs123@users.noreply.github.com>"

Write-Host "====================================="
Write-Host "Git History Successfully Created!"
Write-Host "====================================="
