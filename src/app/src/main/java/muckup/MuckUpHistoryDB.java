package muckup;


import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import dk.seahawk.locator.business.algorithm.GridAlgorithm;
import dk.seahawk.locator.business.algorithm.GridAlgorithmInterface;
import dk.seahawk.model.history.SaveObject;

public class MuckUpHistoryDB {
    private final LinkedList<SaveObject> hashMap;
    private final GridAlgorithmInterface gridAlgorithmInterface;

    public MuckUpHistoryDB() {
        this.hashMap = new LinkedList<>();
        this.gridAlgorithmInterface = new GridAlgorithm();
    }

    public void putElement(SaveObject saveObject) {
        hashMap.add(saveObject);
    }

    public SaveObject getFirstElement() {
        return hashMap.getLast();
    }

    public SaveObject getLastElement() {
        return hashMap.getLast();
    }

    public SaveObject getElementById(int id) {
        return hashMap.get(id);
    }

    public void removeElementById(int id) {
        hashMap.remove(id);
    }

    public boolean dbIsEmpty() {
        return hashMap.isEmpty();
    }

    public int size() {
        return hashMap.size();
    }

    public void fillDummyList () {

        MuckUpLocation muckUpLocationA = new MuckUpLocation(18.388544, -33.934562, 57.3, 0.125f);
        String gridLocationA = gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocationA);
        SaveObject saveObjectA = new SaveObject(gridLocationA, muckUpLocationA);
        hashMap.add(saveObjectA);
        dummySleep();

        MuckUpLocation muckUpLocationB = new MuckUpLocation(9.563663, 55.887282, 57.3, 0.125f);
        String gridLocationB = gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocationB);
        SaveObject saveObjectB = new SaveObject(gridLocationB, muckUpLocationB);
        hashMap.add(saveObjectB);
        dummySleep();

        MuckUpLocation muckUpLocationC = new MuckUpLocation(9.8860909, 55.8719648, 63.2, 0.513f);
        String gridLocationC = gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocationC);
        SaveObject saveObjectC = new SaveObject(gridLocationC, muckUpLocationC);
        hashMap.add(saveObjectC);
        dummySleep();

        MuckUpLocation muckUpLocationD = new MuckUpLocation(-51.1967887, 68.8197614, 26.8, 0.213f);
        String gridLocationD = gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocationD);
        SaveObject saveObjectD = new SaveObject(gridLocationD, muckUpLocationD);
        hashMap.add(saveObjectD);
        dummySleep();

        MuckUpLocation muckUpLocationE = new MuckUpLocation(18.388544, -33.934562, 57.3, 0.125f);
        String gridLocationE = gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocationE);
        SaveObject saveObjectE = new SaveObject(gridLocationE, muckUpLocationE);
        hashMap.add(saveObjectE);
        dummySleep();

        MuckUpLocation muckUpLocationF = new MuckUpLocation(9.563663, 55.887282, 57.3, 0.125f);
        String gridLocationF = gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocationF);
        SaveObject saveObjectF = new SaveObject(gridLocationF, muckUpLocationF);
        hashMap.add(saveObjectF);
        dummySleep();

        MuckUpLocation muckUpLocationG = new MuckUpLocation(9.8860909, 55.8719648, 63.2, 0.513f);
        String gridLocationG = gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocationC);
        SaveObject saveObjectG = new SaveObject(gridLocationG, muckUpLocationG);
        hashMap.add(saveObjectG);
        dummySleep();

        MuckUpLocation muckUpLocationH = new MuckUpLocation(-51.1967887, 68.8197614, 26.8, 0.213f);
        String gridLocationH = gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocationH);
        SaveObject saveObjectH = new SaveObject(gridLocationH, muckUpLocationH);
        hashMap.add(saveObjectH);
        dummySleep();

        MuckUpLocation muckUpLocationI = new MuckUpLocation(18.388544, -33.934562, 57.3, 0.125f);
        String gridLocationI = gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocationI);
        SaveObject saveObjectI = new SaveObject(gridLocationI, muckUpLocationI);
        hashMap.add(saveObjectI);
        dummySleep();

        MuckUpLocation muckUpLocationJ = new MuckUpLocation(9.563663, 55.887282, 57.3, 0.125f);
        String gridLocationJ = gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocationJ);
        SaveObject saveObjectJ = new SaveObject(gridLocationJ, muckUpLocationJ);
        hashMap.add(saveObjectJ);
        dummySleep();

        MuckUpLocation muckUpLocationK = new MuckUpLocation(9.8860909, 55.8719648, 63.2, 0.513f);
        String gridLocationK = gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocationK);
        SaveObject saveObjectK = new SaveObject(gridLocationK, muckUpLocationK);
        hashMap.add(saveObjectK);
        dummySleep();

        MuckUpLocation muckUpLocationL = new MuckUpLocation(-51.1967887, 68.8197614, 26.8, 0.213f);
        String gridLocationL = gridAlgorithmInterface.getGridLocationTestMethod(muckUpLocationL);
        SaveObject saveObjectL = new SaveObject(gridLocationL, muckUpLocationL);
        hashMap.add(saveObjectL);

    }

    private void dummySleep() {
        Random random = new Random();
        TimeUnit time = TimeUnit.MILLISECONDS;

        try {
            time.sleep(random.nextInt(500));
        } catch (InterruptedException e) {
            System.out.println("HistoryDB.dummySleep Throws InterruptedException");
        }
    }

}
