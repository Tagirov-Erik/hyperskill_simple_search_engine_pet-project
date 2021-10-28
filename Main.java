package search;
import java.io.*;

import java.util.*;

public class Main {

    interface SearchingStrategy {
        void strategy(Map<String, HashSet<Integer>> invertedIndexMap, String query, List<String> inputs);
    }

    static class DoSearchByStrategy {
        private SearchingStrategy searchingStrategy;

        public void setSearchingStrategy(SearchingStrategy strategy) {
            this.searchingStrategy = strategy;
        }

        public void strategy(Map<String, HashSet<Integer>> invertedIndexMap, String query, List<String> inputs) {
            this.searchingStrategy.strategy(invertedIndexMap,query, inputs);
        }

    }

    static class AllSearchStrategy implements SearchingStrategy {
        @Override
        public void strategy(Map<String, HashSet<Integer>> invertedIndexMap, String query, List<String> inputs) {
            String[] fullQuery = query.toLowerCase().split(" ");
            List<Integer> checkQuery = new ArrayList<>();
            int isDone;
            HashSet<Integer> res = new HashSet<>();

            if (fullQuery.length > 1) {
                for (String element : fullQuery) {
                    if (invertedIndexMap.get(element) != null) {
                        HashSet<Integer> lines = invertedIndexMap.get(element);
                        System.out.println(lines);
                        checkQuery.addAll(lines);
                    }
                }

                for (int i = 0; i < checkQuery.size(); i++) {
                    isDone = 1;
                    for (int j = i + 1; j < checkQuery.size(); j++) {
                        if (Objects.equals(checkQuery.get(i), checkQuery.get(j))) {
                            isDone++;
                        }
                        if (isDone == fullQuery.length) {
                            res.add(checkQuery.get(i));
                            break;
                        }
                    }
                }

                for (Integer element : res) {
                    System.out.println(inputs.get(element));
                }
            } else {
                if (invertedIndexMap.get(query.toLowerCase()) != null) {
                    HashSet<Integer> lines = invertedIndexMap.get(query.toLowerCase());
                    System.out.println(lines.size() + " person found:");
                    for (Integer element : lines) {
                        System.out.println(inputs.get(element));
                    }
                } else {
                    System.out.println("No matching people found.");
                }
            }
        }
    }

    static class AnySearchStrategy implements SearchingStrategy {
        @Override
        public void strategy(Map<String, HashSet<Integer>> invertedIndexMap, String query, List<String> inputs) {
            String[] fullQuery = query.toLowerCase().split(" ");
            HashSet<Integer> res = new HashSet<>();

            for (String element : fullQuery) {
                if (invertedIndexMap.get(element) != null) {
                    HashSet<Integer> lines = invertedIndexMap.get(element);
                    res.addAll(lines);
                }
            }

            for (Integer element : res) {
                System.out.println(inputs.get(element));
            }
        }
    }

    static class NoneSearchStrategy implements SearchingStrategy{
        @Override
        public void strategy(Map<String, HashSet<Integer>> invertedIndexMap, String query, List<String> inputs) {
            String[] fullQuery = query.toLowerCase().split(" ");
            HashSet<Integer> res = new HashSet<>();
            HashSet<Integer> inputsIndex = new HashSet<>();

            for (String element : fullQuery) {
                if (invertedIndexMap.get(element) != null) {
                    HashSet<Integer> lines = invertedIndexMap.get(element);
                    res.addAll(lines);
                }
            }
            for (int i = 0; i < inputs.size(); i++) {
                inputsIndex.add(i);
            }

            inputsIndex.removeAll(res);

            for (Integer el : inputsIndex) {
                System.out.println(inputs.get(el));
            }
        }
    }

    public static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException {

        String filePath = "";
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--data")) {
                filePath = args[i + 1];
            }
        }

        File inputFile = new File(filePath);

        List<String> inputs = new ArrayList<>();

        assert filePath != null;
        try (Scanner fileScan = new Scanner(inputFile)) {
            while (fileScan.hasNext()) {
                inputs.add(fileScan.nextLine());
            }
        }


        DoSearchByStrategy searchByStrategy = new DoSearchByStrategy();



        while (true) {
            showMenu();
            switch (scan.nextLine()) {
                case "1":
                    System.out.println("Select a matching strategy: ALL, ANY, NONE");
                    String str = scan.nextLine();
                    switch (str) {
                        case "ALL": {
                            searchByStrategy.setSearchingStrategy(new AllSearchStrategy());
                            System.out.println("Enter a name or email to search all suitable people.");
                            String query = scan.nextLine();
                            searchByStrategy.strategy(invertedIndexBuild(filePath), query, inputs);
                            break;
                        }
                        case "ANY": {
                            searchByStrategy.setSearchingStrategy(new AnySearchStrategy());
                            System.out.println("Enter a name or email to search all suitable people.");
                            String query = scan.nextLine();
                            searchByStrategy.strategy(invertedIndexBuild(filePath), query, inputs);
                            break;
                        }
                        case "NONE": {
                            searchByStrategy.setSearchingStrategy(new NoneSearchStrategy());
                            System.out.println("Enter a name or email to search all suitable people.");
                            String query = scan.nextLine();
                            searchByStrategy.strategy(invertedIndexBuild(filePath), query, inputs);
                            break;
                        }
                        default:
                            System.out.println("Incorrect option! Try again.");
                    }
//                    printSearchResults(invertedIndexBuild(filePath), query, inputs);
                    break;
                case "2":
                    printAll(inputs);
                    break;
                case "0":
                    System.out.println("Bye!");
                    System.exit(0);
                default:
                    System.out.println("Incorrect option! Try again.");
            }
        }
    }

    //old version for search
    public static void printSearchResults(List<String> inputLines, String query) {
        boolean isFound = false;
        for (String line : inputLines) {
            String lowerLine = line.toLowerCase();
            if (lowerLine.contains(query.toLowerCase())) {
                System.out.println(line);
                isFound = true;
            }
        }
        if (!isFound) {
            System.out.println("No matches");
        }
    }

    public static void printSearchResults(Map<String, HashSet<Integer>> invertedIndexMap, String query, List<String> inputs ) {


        if (invertedIndexMap.get(query.toLowerCase()) != null) {
            HashSet<Integer> lines = invertedIndexMap.get(query.toLowerCase());
            System.out.println(lines.size() + " person found:");
            for (Integer element : lines) {
                System.out.println(inputs.get(element));
            }
        } else {
            System.out.println("No matching people found.");
        }

    }

    public static void showMenu() {
        System.out.println("=== Menu ===\n" +
                "1. Find a person\n" +
                "2. Print all people\n" +
                "0. Exit\n");
    }

    public static void printAll(List<String> inputLines) {
        System.out.println();
        System.out.println("=== List of people ===");
        for (String line : inputLines) {
            System.out.println(line);
        }
        System.out.println();
    }

    public static Map<String, HashSet<Integer>> invertedIndexBuild(String fileName) {
        Map<String, HashSet<Integer>> invertedIndex = new HashMap<>();
        int i = 0;
        try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
            String ln;
            while ((ln = file.readLine()) != null) {
                String[] words = ln.split(" ");
                for (String word : words) {
                    word = word.toLowerCase();
                    if (!invertedIndex.containsKey(word)) {
                        invertedIndex.put(word, new HashSet<>());
                    }
                    invertedIndex.get(word).add(i);
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return invertedIndex;
    }
}
