package com.uraltranscom.dynamicdistributionpark.service.impl;

import com.uraltranscom.dynamicdistributionpark.model.Wagon;
import com.uraltranscom.dynamicdistributionpark.service.GetList;
import com.uraltranscom.dynamicdistributionpark.service.export.WriteToFileExcel;
import com.uraltranscom.dynamicdistributionpark.util.PropertyUtil;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Класс получения списка вагонов
 * Implementation for {@link GetList} interface
 *
 * @author Vladislav Klochkov
 * @version 1.0
 * @create 19.07.2018
 *
 * 19.07.2018
 *   1. Версия 1.0
 *
 */

@Service
public class GetListOfWagonsImpl implements GetList {
    // Подключаем логгер
    private static Logger logger = LoggerFactory.getLogger(GetListOfWagonsImpl.class);

    // Основаная мапа, куда записываем все вагоны
    private List<Wagon> listOfWagons = new ArrayList<>();

    // Переменные для работы с файлами
    private File file ;
    private FileInputStream fileInputStream;

    // Переменные для работы с Excel файлом(формат XLSX)
    private XSSFWorkbook xssfWorkbook;
    private XSSFSheet sheet;

    @Autowired
    private WriteToFileExcel writeToFileExcel;

    @Autowired
    private PropertyUtil propertyUtil;

    private GetListOfWagonsImpl() {
    }

    // Заполняем Map вагонами
    // TODO Переписать метод, отвязать от количества строк, избавиться от формата жесткого, необходимо и XLSX и XLS
    @Override
    public void fillMap() {
        listOfWagons.clear();
        writeToFileExcel.setFile(null);
        writeToFileExcel.setFile(file);

        // Получаем файл формата xls
        try {
            fileInputStream = new FileInputStream(this.file);
            xssfWorkbook = new XSSFWorkbook(fileInputStream);

            // Заполняем мапу данными
            sheet = xssfWorkbook.getSheetAt(0);
            for (int j = 1; j < sheet.getLastRowNum() + 1; j++) {
                XSSFRow row = sheet.getRow(0);

                String numberOfWagon = null;
                String keyOfStationDestination = null;
                String nameOfStationDestination = null;
                int volume = 0;
                String nameCargo = null;
                String keyCargo = null;

                for (int c = 0; c < row.getLastCellNum(); c++) {
                    if (row.getCell(c).getStringCellValue().trim().equals(propertyUtil.getProperty("wagon.numberwagon"))) {
                        XSSFRow xssfRow = sheet.getRow(j);
                        numberOfWagon = xssfRow.getCell(c).getStringCellValue();
                    }
                    if (row.getCell(c).getStringCellValue().trim().equals(propertyUtil.getProperty("wagon.namestationdestination"))) {
                        XSSFRow xssfRow = sheet.getRow(j);
                        nameOfStationDestination = xssfRow.getCell(c).getStringCellValue();
                    }
                    if (row.getCell(c).getStringCellValue().trim().equals(propertyUtil.getProperty("wagon.keystationdestination"))) {
                        XSSFRow xssfRow = sheet.getRow(j);
                        keyOfStationDestination = xssfRow.getCell(c).getStringCellValue();
                    }
                    if (row.getCell(c).getStringCellValue().trim().equals(propertyUtil.getProperty("wagon.volume"))) {
                        XSSFRow xssfRow = sheet.getRow(j);
                        volume = (int) xssfRow.getCell(c).getNumericCellValue();
                    }
                    if (row.getCell(c).getStringCellValue().trim().equals(propertyUtil.getProperty("wagon.namecargo"))) {
                        XSSFRow xssfRow = sheet.getRow(j);
                        nameCargo = xssfRow.getCell(c).getStringCellValue();
                    }
                    if (row.getCell(c).getStringCellValue().trim().equals(propertyUtil.getProperty("wagon.keycargo"))) {
                        XSSFRow xssfRow = sheet.getRow(j);
                        keyCargo = xssfRow.getCell(c).getStringCellValue();
                    }
                }
                listOfWagons.add(new Wagon(numberOfWagon, keyOfStationDestination, nameOfStationDestination, volume, nameCargo, keyCargo));
            }
            logger.debug("Body wagon: {}", listOfWagons);
        } catch (IOException e) {
            logger.error("Ошибка загруки файла - {}", e.getMessage());
        } catch (OLE2NotOfficeXmlFileException e1) {
            logger.error("Некорректный формат файла дислокации, необходим формат xlsx");
        }

    }

    public List<Wagon> getListOfWagons() {
        return listOfWagons;
    }

    public void setListOfWagons(List<Wagon> listOfWagons) {
        this.listOfWagons = listOfWagons;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        fillMap();
    }
}