package p.a;

public class Good {
    private String title;
    private String price;
    private String url;
    private String number;

    @Override
    public String toString() {
        return String.format("<table border=\"1\" style=\"border-collapse: collapse; width: 100%%; table-layout: fixed;\">" +
                "<colgroup>" +
                "<col style=\"width: 50%%;\">" +
                "<col style=\"width: 25%%;\">" +
                "<col style=\"width: 25%%;\">" +
                "</colgroup>" +
                "<tr>" +
                "<td style=\"word-wrap: break-word;\"><a href=\"%s\" target=\"_blank\">%s</a></td>" +
                "<td style=\"word-wrap: break-word; text-align: center;\">%s</td>" +
                "<td style=\"word-wrap: break-word; text-align: center;\">%s</td>" +
                "</tr>" +
                "</table>", url, title, price, number);
    }

    public Good(String title, String price, String url, String number) {
        this.title = title;
        this.price = price;
        this.url = url;
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
