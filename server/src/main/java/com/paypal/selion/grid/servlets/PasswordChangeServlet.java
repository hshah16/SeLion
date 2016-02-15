/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
|                                                                                                                     |
|  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     |
|  with the License.                                                                                                  |
|                                                                                                                     |
|  You may obtain a copy of the License at                                                                            |
|                                                                                                                     |
|       http://www.apache.org/licenses/LICENSE-2.0                                                                    |
|                                                                                                                     |
|  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   |
|  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  |
|  the specific language governing permissions and limitations under the License.                                     |
\*-------------------------------------------------------------------------------------------------------------------*/

package com.paypal.selion.grid.servlets;

import com.paypal.selion.utils.AuthenticationHelper;
import com.paypal.selion.utils.ServletHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This servlet provides the ability to change the password for servlets which require/use {@link LoginServlet}
 */
public class PasswordChangeServlet extends HttpServlet {

    /**
     * Resource path to the password change html template file
     */
    public static final String RESOURCE_PAGE_FILE = "/com/paypal/selion/html/passwordChangeServlet.html";

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        askForCredentialsPage(writer);
    }

    private void askForCredentialsPage(PrintWriter writer) throws IOException {
        loadPage(writer, "Fill out the form to change the management console password");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();

        String userid = (String) req.getSession().getAttribute("userId");
        String oldPassword = req.getParameter("oldPassword");
        String newPassword1 = req.getParameter("newPassword1");
        String newPassword2 = req.getParameter("newPassword2");

        if (!newPassword1.contentEquals(newPassword2) || newPassword1 == null || newPassword2 == null) {
            loadPage(writer, "<b>The new passwords do not match</b>");
        } else if (!AuthenticationHelper.authenticate(userid, oldPassword)) {
            loadPage(writer, "<b>The old password did not match the one on record</b>");

        } else if (!AuthenticationHelper.changePassword(userid, newPassword1)) {
            loadPage(writer, "<b>Something went wrong while changing the password.</b>");
        } else {
            HttpSession session = req.getSession(false);
            if (session != null) {
                // invalidating the current session so that the password change is reflected in the forth coming session
                session.invalidate();
            }
            ServletHelper.displayMessageOnRedirect(writer, "<p align='center'><b>Password changed</b></p>");
        }
    }

    private void loadPage(PrintWriter writer, String errorMessage) throws IOException {
        ServletHelper.respondWithTemplate(writer, RESOURCE_PAGE_FILE, errorMessage);
    }
}
