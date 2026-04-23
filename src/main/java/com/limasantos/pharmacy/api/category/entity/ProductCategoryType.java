package com.limasantos.pharmacy.api.category.entity;

public enum ProductCategoryType {

    // CORREÇÃO: Todos os itens na lista, separados por vírgula
    MEDICAMENTOS("Medicamentos"),
    GENERICOS("Genéricos"),
    SIMILARES("Similares"),
    PERFUMARIA("Perfumaria"),
    HIGIENE_PESSOAL("Higiene Pessoal"),
    INFANTIL("Linha Infantil"),
    DERMOCOSMETICOS("Dermocosméticos"),
    SAUDE_SEXUAL("Saúde Sexual"), // Adicionado corretamente
    SUPLEMENTOS("Suplementos e Vitaminas"); // Ponto e vírgula vai aqui, no final da lista


    private final String displayName;

    ProductCategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
