package com.darwgom.tradibankapi.application.usecases.implement;

import com.darwgom.tradibankapi.application.dto.MessageDTO;
import com.darwgom.tradibankapi.application.dto.ReportDTO;
import com.darwgom.tradibankapi.application.dto.ReportInputDTO;
import com.darwgom.tradibankapi.application.usecases.IReportUseCase;
import com.darwgom.tradibankapi.domain.entities.Report;
import com.darwgom.tradibankapi.domain.entities.Transaction;
import com.darwgom.tradibankapi.domain.enums.ReportTypeEnum;
import com.darwgom.tradibankapi.domain.enums.TransactionTypeEnum;
import com.darwgom.tradibankapi.domain.repositories.ReportRepository;
import com.darwgom.tradibankapi.domain.repositories.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;

import java.util.Base64;

@Service
public class ReportUseCase implements IReportUseCase {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public byte[] generateMonthlyStatement(Long accountId, YearMonth month) throws IOException {
        List<Transaction> transactions = transactionRepository.findByAccountIdAndMonthAndYear(accountId, month.getMonthValue(), month.getYear());

        BigDecimal initialBalance = calculateInitialBalance(accountId, month);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 700);

                contentStream.showText("Extracto Mensual para Cuenta ID: " + accountId + " - Mes: " + month);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Saldo Inicial: " + initialBalance);
                contentStream.newLineAtOffset(0, -15);

                for (Transaction transaction : transactions) {
                    contentStream.showText(formatTransactionLine(transaction));
                    contentStream.newLineAtOffset(0, -15);
                }

                BigDecimal finalBalance = calculateFinalBalance(initialBalance, transactions);
                contentStream.showText("Saldo Final: " + finalBalance);
                contentStream.endText();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            saveMonthlyStatement(out.toByteArray());
            return out.toByteArray();
        }
    }

    @Override
    public MessageDTO saveMonthlyStatement(byte[] generateMonthlyStatement) {
        String base64Encoded = Base64.getEncoder().encodeToString(generateMonthlyStatement);
        Report report = new Report();
        report.setDetails(base64Encoded);
        report.setReportType(ReportTypeEnum.MONTHLY_STATEMENT);
        report.setCreationDate(new Date());

        reportRepository.save(report);
        return new MessageDTO("Monthly statement saved successfully.");
    }

    private String formatTransactionLine(Transaction transaction) {
        return transaction.getTransactionDate().toString() + " - " + transaction.getTransactionType() + " - Monto: " + transaction.getAmount();
    }

    private BigDecimal calculateInitialBalance(Long accountId, YearMonth month) {
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateFinalBalance(BigDecimal initialBalance, List<Transaction> transactions) {
        BigDecimal finalBalance = initialBalance;
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType() == TransactionTypeEnum.DEPOSIT) {
                finalBalance = finalBalance.add(transaction.getAmount());
            } else if (transaction.getTransactionType() == TransactionTypeEnum.WITHDRAWAL) {
                finalBalance = finalBalance.subtract(transaction.getAmount());
            }
        }
        return finalBalance;
    }

}
