
package com.myfin.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminLoanController {

    @GetMapping("/admin/loans")
    public String showLoansPage() {
        return "admin-loans"; // src/main/resources/templates/admin-loans.html
    }
}
