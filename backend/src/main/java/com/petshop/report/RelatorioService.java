package com.petshop.report;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.petshop.dto.LancamentoServicoDTO;
import com.petshop.dto.ProprietarioDTO;
import com.petshop.dto.AnimalDTO;
import com.petshop.service.AnimalService;
import com.petshop.service.LancamentoServicoService;
import com.petshop.service.ProprietarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final ProprietarioService proprietarioService;
    private final AnimalService animalService;
    private final LancamentoServicoService lancamentoService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DeviceRgb COR_PRIMARIA = new DeviceRgb(25, 118, 210);
    private static final DeviceRgb COR_HEADER = new DeviceRgb(236, 239, 241);

    public byte[] gerarRelatorioCliente(Long proprietarioId, LocalDate dataInicio, LocalDate dataFim) {
        ProprietarioDTO proprietario = proprietarioService.buscarPorId(proprietarioId);
        List<AnimalDTO> animais = animalService.buscarPorProprietario(proprietarioId);
        List<LancamentoServicoDTO> lancamentos;

        if (dataInicio != null && dataFim != null) {
            lancamentos = lancamentoService.buscarPorProprietarioEPeriodo(proprietarioId, dataInicio, dataFim);
        } else {
            lancamentos = lancamentoService.buscarPorProprietario(proprietarioId);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        adicionarCabecalho(document, "Relatório de Serviços por Cliente");

        adicionarSecao(document, "Dados do Cliente");
        adicionarInfoCliente(document, proprietario);

        adicionarSecao(document, "Animais Cadastrados");
        adicionarTabelaAnimais(document, animais);

        adicionarSecao(document, "Serviços Prestados" +
                (dataInicio != null ? " — " + dataInicio.format(DATE_FORMATTER) +
                        " a " + dataFim.format(DATE_FORMATTER) : ""));
        adicionarTabelaServicos(document, lancamentos);

        adicionarTotaisPorServico(document, lancamentos);
        adicionarRodape(document);

        document.close();
        return baos.toByteArray();
    }

    private void adicionarCabecalho(Document document, String titulo) {
        Paragraph header = new Paragraph("PetShop Manager")
                .setFontSize(10)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(header);

        Paragraph title = new Paragraph(titulo)
                .setFontSize(20)
                .setBold()
                .setFontColor(COR_PRIMARIA)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(title);

        Paragraph dataParagraph = new Paragraph("Gerado em: " + LocalDate.now().format(DATE_FORMATTER))
                .setFontSize(9)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(dataParagraph);
    }

    private void adicionarSecao(Document document, String titulo) {
        Paragraph secao = new Paragraph(titulo)
                .setFontSize(13)
                .setBold()
                .setFontColor(COR_PRIMARIA)
                .setMarginTop(15)
                .setMarginBottom(8);
        document.add(secao);
    }

    private void adicionarInfoCliente(Document document, ProprietarioDTO p) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        adicionarLinhaInfo(table, "Nome:", p.getNome());
        adicionarLinhaInfo(table, "Email:", p.getEmail());
        adicionarLinhaInfo(table, "Telefone:", p.getTelefone());
        adicionarLinhaInfo(table, "Endereço:", p.getEndereco());

        document.add(table);
    }

    private void adicionarLinhaInfo(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold().setFontSize(10))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f)));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "-").setFontSize(10))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f)));
    }

    private void adicionarTabelaAnimais(Document document, List<AnimalDTO> animais) {
        if (animais.isEmpty()) {
            document.add(new Paragraph("Nenhum animal cadastrado.").setFontColor(ColorConstants.GRAY));
            return;
        }

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1.5f, 1.5f, 0.8f, 0.8f, 0.8f}))
                .setWidth(UnitValue.createPercentValue(100));

        adicionarHeaderTabela(table, "Nome", "Espécie", "Raça", "Idade", "Sexo", "Peso (kg)");

        for (AnimalDTO a : animais) {
            table.addCell(celula(a.getNome()));
            table.addCell(celula(a.getEspecie()));
            table.addCell(celula(a.getRaca()));
            table.addCell(celula(a.getIdade() + " anos"));
            table.addCell(celula(a.getSexo()));
            table.addCell(celula(String.format("%.2f", a.getPeso())));
        }
        document.add(table);
    }

    private void adicionarTabelaServicos(Document document, List<LancamentoServicoDTO> lancamentos) {
        if (lancamentos.isEmpty()) {
            document.add(new Paragraph("Nenhum serviço registrado no período.").setFontColor(ColorConstants.GRAY));
            return;
        }

        Table table = new Table(UnitValue.createPercentArray(new float[]{1.5f, 2, 2, 1.5f, 3}))
                .setWidth(UnitValue.createPercentValue(100));

        adicionarHeaderTabela(table, "Data", "Animal", "Serviço", "Valor (R$)", "Observações");

        for (LancamentoServicoDTO l : lancamentos) {
            table.addCell(celula(l.getData().format(DATE_FORMATTER)));
            table.addCell(celula(l.getAnimalNome()));
            table.addCell(celula(l.getServicoNome()));
            table.addCell(celula(String.format("%.2f", l.getValor())));
            table.addCell(celula(l.getObservacoes() != null ? l.getObservacoes() : "-"));
        }
        document.add(table);

        BigDecimal total = lancamentos.stream()
                .map(LancamentoServicoDTO::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Paragraph totalParagraph = new Paragraph("Total Geral: R$ " + String.format("%.2f", total))
                .setBold()
                .setFontSize(12)
                .setFontColor(COR_PRIMARIA)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(5);
        document.add(totalParagraph);
    }

    private void adicionarTotaisPorServico(Document document, List<LancamentoServicoDTO> lancamentos) {
        if (lancamentos.isEmpty()) return;

        adicionarSecao(document, "Total por Serviço");

        Map<String, BigDecimal> totalPorServico = lancamentos.stream()
                .collect(Collectors.groupingBy(
                        LancamentoServicoDTO::getServicoNome,
                        Collectors.reducing(BigDecimal.ZERO, LancamentoServicoDTO::getValor, BigDecimal::add)
                ));

        Map<String, Long> quantidadePorServico = lancamentos.stream()
                .collect(Collectors.groupingBy(LancamentoServicoDTO::getServicoNome, Collectors.counting()));

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 2}))
                .setWidth(UnitValue.createPercentValue(60));

        adicionarHeaderTabela(table, "Serviço", "Qtd.", "Total (R$)");

        for (Map.Entry<String, BigDecimal> entry : totalPorServico.entrySet()) {
            table.addCell(celula(entry.getKey()));
            table.addCell(celula(String.valueOf(quantidadePorServico.get(entry.getKey()))));
            table.addCell(celula(String.format("%.2f", entry.getValue())));
        }
        document.add(table);
    }

    private void adicionarHeaderTabela(Table table, String... headers) {
        for (String header : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setBold().setFontSize(10).setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(COR_PRIMARIA));
        }
    }

    private Cell celula(String text) {
        return new Cell()
                .add(new Paragraph(text != null ? text : "-").setFontSize(9))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
    }

    private void adicionarRodape(Document document) {
        Paragraph rodape = new Paragraph("PetShop Manager — Sistema de Gerenciamento de Pet Shop")
                .setFontSize(8)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30);
        document.add(rodape);
    }
}
