package com.banking.infrastructure.output.adapter;

import com.banking.application.output.port.BankStatementReportService;
import com.banking.domain.AbstractBankStatementInput;
import com.banking.domain.BankStatementInput;
import com.banking.domain.BankStatementOutput;
import com.banking.domain.BankStatementPdfInput;
import com.banking.infrastructure.output.repository.BankStatementJpaRepository;
import com.banking.infrastructure.output.repository.mapper.BankStatementMapper;
import com.banking.infrastructure.output.repository.projection.BankStatementProjection;
import com.banking.infrastructure.util.StreamUtils;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BankStatementReportServiceAdapter implements BankStatementReportService {
    private static final String BANK_STATEMENT_PDF_TITLE = "Bank Statement";
    private static final String BANK_STATEMENT_STATUS_COMPLETED = "Completed";
    private static final String BANK_STATEMENT_STATUS_FAILED = "Failed";
    private static final Font BOLD_FONT = new Font(Font.HELVETICA, 12, Font.BOLD);

    BankStatementJpaRepository bankStatementJpaRepository;
    BankStatementMapper bankStatementProjectionMapper;

    @NonNull
    private Mono<Page<BankStatementProjection>> getBankStatementData(
        @NonNull final AbstractBankStatementInput abstractBankStatementInput, @NonNull final Integer page,
        @NonNull final Integer size) {
        final var sort = Sort.by(Sort.Order.desc("date"), Sort.Order.desc("id"));
        return Mono.fromCallable(() ->
            bankStatementJpaRepository.getBankStatement(
                abstractBankStatementInput.getCustomerId(),
                abstractBankStatementInput.getStartDate(),
                abstractBankStatementInput.getEndDate(),
                PageRequest.of(page, size, sort)
            )
        );
    }

    @NonNull
    @Override
    public Mono<Page<BankStatementOutput>> getBankStatement(
        @NonNull @NotNull @Valid final BankStatementInput bankStatementInput) {
        final var bankStatementInputString = bankStatementInput.toString();
        log.info("Getting bank statement: input={}", bankStatementInputString);
        return getBankStatementData(bankStatementInput, bankStatementInput.getPage(), bankStatementInput.getSize())
            .map(bankStatementProjectionPage ->
                bankStatementProjectionPage.map(bankStatementProjectionMapper::toBankStatementOutput))
            .doOnSuccess(paginatedBankStatement -> log.info("Bank statement was gotten: input={}, total items={}",
                bankStatementInputString, paginatedBankStatement.getTotalElements()))
            .doOnError(error -> log.error("Error getting bank statement: input={}, error={}", bankStatementInputString,
                error.getMessage()));
    }

    @NonNull
    @Override
    public Mono<byte[]> generateBankStatementPdf(
        @NonNull @NotNull @Valid final BankStatementPdfInput bankStatementPdfInput) {
        final var bankStatementPdfInputString = bankStatementPdfInput.toString();
        log.info("Generating bank statement PDF: input={}", bankStatementPdfInputString);
        return getBankStatementData(bankStatementPdfInput, 0, Integer.MAX_VALUE)
            .map(Page::getContent)
            .flatMap(BankStatementReportServiceAdapter::buildBankStatementPdf)
            .doOnSuccess(bankStatementPdf ->
                log.info("Bank statement PDF was generated: input={}, PDF length={}", bankStatementPdfInputString,
                    bankStatementPdf.length))
            .doOnError(error ->
                log.error("Error generating bank statement PDF: input={}, error={}", bankStatementPdfInputString,
                    error.getMessage()));
    }

    @NonNull
    private static Mono<PdfDocumentWrapper> openPdfDocument() {
        return Mono.fromCallable(() -> {
            final var document = new Document();
            final var outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            return new PdfDocumentWrapper(document, outputStream);
        });
    }

    @NonNull
    private static Mono<Paragraph> buildBankStatementPdfTitleParagraph() {
        return Mono.just(new Paragraph(BANK_STATEMENT_PDF_TITLE, BOLD_FONT));
    }

    @NonNull
    private static Mono<Paragraph> buildBankStatementPdfCustomerParagraph(
        @NonNull final List<BankStatementProjection> bankStatements) {
        return Mono.just(bankStatements)
            .filter(theStatements -> !theStatements.isEmpty())
            .map(List::getFirst)
            .map(firstBankStatement ->
                String.format(
                    "%nCustomer: %s%nAccount number: %s%nInitial balance: %s%n%n",
                    firstBankStatement.getCustomer(),
                    firstBankStatement.getAccountNumber(),
                    firstBankStatement.getInitialBalance()
                )
            )
            .map(Paragraph::new);
    }

    @NonNull
    private static Mono<Paragraph> buildBankStatementPdfItemParagraph(
        @NonNull final BankStatementProjection bankStatement) {
        return Mono.just(bankStatement)
            .map(theBankStatement ->
                String.format(
                    "Id: %08d | Date: %s | Type: %s | Status: %s | Value: %s | Balance: %s",
                    theBankStatement.getId(),
                    theBankStatement.getDate(),
                    theBankStatement.getType(),
                    Boolean.TRUE.equals(theBankStatement.getStatus())
                        ? BANK_STATEMENT_STATUS_COMPLETED
                        : BANK_STATEMENT_STATUS_FAILED,
                    theBankStatement.getValue(),
                    theBankStatement.getBalance()
                )
            )
            .map(Paragraph::new);
    }

    @NonNull
    private static Mono<byte[]> buildBankStatementPdf(@NonNull final List<BankStatementProjection> statements) {
        return openPdfDocument().flatMap(pdfDocumentWrapper -> {
            final var document = pdfDocumentWrapper.document;
            final var outputStream = pdfDocumentWrapper.outputStream;
            return buildBankStatementPdfTitleParagraph()
                .map(document::add)
                .flatMap(isTitleAdded -> buildBankStatementPdfCustomerParagraph(statements))
                .map(document::add)
                .flatMapMany(isCustomerDataAdded ->
                    Flux.fromIterable(statements)
                        .flatMap(BankStatementReportServiceAdapter::buildBankStatementPdfItemParagraph)
                        .map(document::add)
                )
                .count()
                .doOnNext(numberOfItemsWritten -> document.close())
                .map(numberOfItemsWritten -> outputStream.toByteArray())
                .doOnTerminate(document::close)
                .doOnTerminate(() -> StreamUtils.closeOutputStream(outputStream));
        });
    }

    private record PdfDocumentWrapper(Document document, ByteArrayOutputStream outputStream) {
    }
}
