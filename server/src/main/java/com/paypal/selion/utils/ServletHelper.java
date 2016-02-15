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

package com.paypal.selion.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.paypal.selion.grid.servlets.LoginServlet;

/**
 * A utility class that basically helps in extracting information from Servlet request/responses also has the commonly
 * load the HTML template from resource and write it to the response.
 * 
 */
public final class ServletHelper {

    private static final String MESSAGE_RESOURCE_PAGE_FILE = "/com/paypal/selion/html/message.html";

    /**
     * Helps retrieve the parameters and its values as a Map
     * 
     * @param request
     *            A {@link HttpServletRequest} that represents the request from which the parameters and their
     *            corresponding values are to be extracted.
     * @return A {@link Map} that represents the parameters and their values
     */
    public static Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration<?> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String key = (String) names.nextElement();
            String value = request.getParameter(key);
            if (StringUtils.isNotEmpty(value)) {
                parameters.put(key, value);
            }
        }
        return parameters;
    }

    /**
     * Sends an HTTP response as a application/json document and with a HTTP status code.
     * 
     * @param resp
     *            A {@link HttpServletResponse} object that the servlet is responding on.
     * @param response
     *            The response object which will be serialized to a JSON document
     * @param statusCode
     *            The HTTP status code to send with the response
     * @throws IOException
     */
    public static void respondAsJsonWithHttpStatus(HttpServletResponse resp, Object response, int statusCode)
            throws IOException {
        String json = new GsonBuilder().serializeNulls().create().toJson(response);
        String jsonUtf8 = new String(json.getBytes(), "UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(statusCode);
        resp.getOutputStream().print(jsonUtf8);
        resp.flushBuffer();
    }

    /**
     * Sends an HTTP response as a text/html document and with a HTTP status code. Injects a json object into the
     * template before responding.
     * 
     * @param resp
     *            A {@link HttpServletResponse} object that the servlet is responding on.
     * @param response
     *            The response object which will be serialized to a JSON document
     * @param resourcePageTemplate
     *            The HTML template to use which is loaded as a classpath resource
     * @param statusCode
     *            The HTTP status code to send with the response
     * @throws IOException
     */
    public static void respondAsHtmlUsingJsonAndTemplateWithHttpStatus(HttpServletResponse resp, Object response,
            String resourcePageTemplate, int statusCode) throws IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        String template = IOUtils.toString(ServletHelper.class.getResourceAsStream(resourcePageTemplate), "UTF-8");
        final String json = new GsonBuilder().serializeNulls().create().toJson(response);
        final String jsonUtf8 = new String(json.getBytes(), "UTF-8");
        template = String.format(template, jsonUtf8);
        resp.getOutputStream().print(template);
        resp.flushBuffer();
    }

    /**
     * Utility method used to display a message when re-direction happens in the UI flow
     * 
     * @param writer
     *            The {@link PrintWriter} object that corresponds to a response
     * @param reDirectMessage
     *            Message to display
     * @throws IOException
     */
    public static void displayMessageOnRedirect(PrintWriter writer, String reDirectMessage) throws IOException {
        respondWithTemplate(writer, MESSAGE_RESOURCE_PAGE_FILE, reDirectMessage);
    }

    /**
     * Utility method to load the template from resource, insert the arguments in template and write to writer
     * 
     * @param writer
     *            The {@link PrintWriter} object that corresponds to a response
     * @param page
     *            HTML template page for response
     * @param args
     *            Data which will be inserted in to the template
     * @throws IOException
     */
    public static void respondWithTemplate(PrintWriter writer, String page, Object... args)
            throws IOException {
        String template = IOUtils.toString(LoginServlet.class.getResourceAsStream(page), "UTF-8");
        writer.write(String.format(template, args));
    }
}
