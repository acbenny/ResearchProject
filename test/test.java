public class test {
    public static void main(String[] args) {
        String serviceId;

        serviceId = String.format("%s%07d", "SVC", 11);
        System.out.println(serviceId);
    }
}
