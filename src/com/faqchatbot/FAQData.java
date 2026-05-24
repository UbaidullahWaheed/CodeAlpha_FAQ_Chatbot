package com.faqchatbot;

import java.util.LinkedHashMap;
import java.util.Map;

public class FAQData {

    public static Map<String, String> getFAQs() {
        Map<String, String> faqs = new LinkedHashMap<>();

        // Admissions
        faqs.put("How do I apply for admission?",
                "You can apply for admission by visiting our university website and filling out the online application form under the Admissions section.");
        faqs.put("What is the last date to apply for admission?",
                "The last date for admission applications is usually July 31st for the fall semester and December 31st for the spring semester.");
        faqs.put("What documents are required for admission?",
                "You need to submit your matric certificate, FSc/A-level result, CNIC copy, domicile certificate, passport-sized photos, and the admission form.");
        faqs.put("Is there an entry test for admission?",
                "Yes, most programs require you to pass a university entry test. Some programs also accept SAT or HAT scores.");
        faqs.put("Can I apply for multiple programs?",
                "Yes, you can apply for up to two programs simultaneously. However, you will be admitted to only one based on merit.");

        // Fees
        faqs.put("What is the tuition fee?",
                "Tuition fees vary by program. Engineering programs cost around PKR 45,000 per semester, while CS programs are around PKR 38,000 per semester.");
        faqs.put("Are there any scholarships available?",
                "Yes, we offer need-based and merit-based scholarships. You can apply through the financial aid office or the university portal.");
        faqs.put("What is the fee submission deadline?",
                "Fee must be submitted within the first two weeks of each semester. Late submissions incur a fine of PKR 500 per day.");
        faqs.put("Can I pay fees in installments?",
                "Yes, the university allows fee payment in two installments per semester. Apply for the installment plan at the accounts office.");
        faqs.put("How do I get a fee receipt?",
                "After paying via bank challan or online portal, you can download your fee receipt from the student portal under the Finance section.");

        // Exams
        faqs.put("When are mid-term exams held?",
                "Mid-term exams are usually held in the 8th or 9th week of the semester.");
        faqs.put("When are final exams held?",
                "Final exams are held at the end of each semester, typically in January (for fall) and June (for spring).");
        faqs.put("How can I apply for rechecking of my paper?",
                "You can apply for rechecking within 7 days of result announcement by submitting a form at the examination office with a fee of PKR 500.");
        faqs.put("What is the passing marks criteria?",
                "You need at least 50% marks in each subject to pass. A GPA of 2.0 or above is required to continue enrollment.");
        faqs.put("Can I give an exam if I have low attendance?",
                "No. You must have at least 75% attendance in a subject to be eligible to sit in the final exam.");

        // Hostel
        faqs.put("Is hostel facility available?",
                "Yes, the university provides separate hostel facilities for male and female students on a first-come, first-served basis.");
        faqs.put("How do I apply for hostel?",
                "You can apply for hostel accommodation through the student affairs office or via the university portal at the start of each semester.");
        faqs.put("What is the hostel fee?",
                "Hostel fee is PKR 15,000 per semester which includes accommodation. Meals are available separately at the cafeteria.");
        faqs.put("Is Wi-Fi available in the hostel?",
                "Yes, all hostel blocks are equipped with high-speed Wi-Fi available 24/7.");

        // Library
        faqs.put("What are the library timings?",
                "The library is open Monday to Saturday from 8:00 AM to 8:00 PM. It remains closed on Sundays and public holidays.");
        faqs.put("How many books can I borrow from the library?",
                "Students can borrow up to 3 books at a time for a period of 14 days. A fine of PKR 10 per day applies on late returns.");
        faqs.put("Does the library have online resources?",
                "Yes, the library provides access to online databases like JSTOR, IEEE Xplore, and Springer through the university portal.");

        // Results & Transcripts
        faqs.put("How can I check my result?",
                "Results are announced on the university portal. Log in with your student ID and go to the Academics section to view your grades.");
        faqs.put("How do I get my official transcript?",
                "You can request an official transcript from the registrar's office. It takes 5-7 working days and costs PKR 500 per copy.");
        faqs.put("What is a GPA and how is it calculated?",
                "GPA stands for Grade Point Average. It is calculated by dividing total grade points earned by total credit hours attempted.");

        // Campus & General
        faqs.put("What are the university timings?",
                "The university is open Monday to Friday from 8:00 AM to 6:00 PM and Saturday from 9:00 AM to 2:00 PM.");
        faqs.put("Is there a transport facility?",
                "Yes, the university provides transport service on designated routes. You can register for transport at the start of each semester.");
        faqs.put("How do I get my student ID card?",
                "Student ID cards are issued by the student affairs office within the first two weeks of enrollment. Bring your admission letter and a passport photo.");
        faqs.put("Is there a medical facility on campus?",
                "Yes, the university has a medical center staffed with a doctor and nurse, open from 9:00 AM to 5:00 PM on weekdays.");
        faqs.put("How do I contact my department?",
                "You can contact your department office directly or email them through the university website's department directory.");

        return faqs;
    }
}