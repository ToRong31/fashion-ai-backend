package com.torome.store.product;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.Map;

@Document(indexName = "products")
public class ProductDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(name = "stock_quantity", type = FieldType.Integer)
    private Integer stockQuantity;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String color;

    @Field(type = FieldType.Keyword)
    private String style;

    @Field(type = FieldType.Keyword)
    private String material;

    @Field(type = FieldType.Keyword)
    private String gender;

    @Field(type = FieldType.Keyword)
    private List<String> season;

    @Field(name = "sizes_available", type = FieldType.Keyword)
    private List<String> sizesAvailable;

    // Full metadata preserved for response mapping
    @Field(type = FieldType.Object, enabled = false)
    private Map<String, Object> metadata;

    public ProductDocument() {}

    @SuppressWarnings("unchecked")
    public static ProductDocument from(ProductEntity entity) {
        ProductDocument doc = new ProductDocument();
        doc.id = String.valueOf(entity.getId());
        doc.name = entity.getName();
        doc.description = entity.getDescription();
        doc.price = entity.getPrice().doubleValue();
        doc.stockQuantity = entity.getStockQuantity();
        doc.metadata = entity.getMetadata();

        if (entity.getMetadata() != null) {
            Map<String, Object> meta = entity.getMetadata();
            doc.category = (String) meta.get("category");
            doc.color = (String) meta.get("color");
            doc.style = (String) meta.get("style");
            doc.material = (String) meta.get("material");
            doc.gender = (String) meta.get("gender");

            Object season = meta.get("season");
            if (season instanceof List<?> list) {
                doc.season = list.stream().map(Object::toString).toList();
            }
            Object sizes = meta.get("sizes_available");
            if (sizes instanceof List<?> list) {
                doc.sizesAvailable = list.stream().map(Object::toString).toList();
            }
        }
        return doc;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public List<String> getSeason() { return season; }
    public void setSeason(List<String> season) { this.season = season; }
    public List<String> getSizesAvailable() { return sizesAvailable; }
    public void setSizesAvailable(List<String> sizesAvailable) { this.sizesAvailable = sizesAvailable; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
