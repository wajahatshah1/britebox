package com.briteboxbackend.briterbox.controller;
import com.briteboxbackend.briterbox.dto.AggregatedBillDTO;
import com.briteboxbackend.briterbox.dto.BillRequest;
import com.briteboxbackend.briterbox.dto.PhoneDistributionDTO;
import com.briteboxbackend.briterbox.entities.Bill;
import com.briteboxbackend.briterbox.repository.BillRepository;
import com.briteboxbackend.briterbox.service.BillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bill")
public class BillController {

    private static final Logger logger = LoggerFactory.getLogger(BillController.class);

    @Autowired
    private BillRepository billRepository;

    private final BillService billService;

    @Autowired
    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping("/saveBill")
    public ResponseEntity<String> saveBill(@RequestBody BillRequest billRequest) {
        try {
            byte[] pdfBytes = Base64.getDecoder().decode(billRequest.getPdf());
            Bill bill = new Bill();
            bill.setCustomerId(billRequest.getCustomerId());
            bill.setName(billRequest.getName());
            bill.setPhoneNumber(billRequest.getPhoneNumber());
            bill.setStatus("unpaid");
            bill.setCleaning(billRequest.isCleaning());
            bill.setReady(billRequest.isReady());
            bill.setDetail(billRequest.isDetail());
            bill.setPickup(billRequest.isPickup());
            bill.setPicked(billRequest.isPicked());
            bill.setDate(LocalDate.now());
            bill.setTotalAmountWithTax(billRequest.getTotalAmountWithTax());
            bill.setPdf(pdfBytes);
            bill.setNotes(billRequest.getNotes());

            billRepository.save(bill);
            return ResponseEntity.ok("Bill saved successfully");
        } catch (Exception e) {
            logger.error("Failed to save bill", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save bill");
        }
    }

    @GetMapping("/billDetails")
    public ResponseEntity<List<Bill>> getDetails() {
        List<Bill> billDetail = billRepository.findByDetail(true);
        return ResponseEntity.ok(billDetail);
    }

    @PutMapping("/updateFlag/{id}/{flag}")
    public ResponseEntity<String> updateFlag(@PathVariable long id, @PathVariable String flag) {
        try {
            Optional<Bill> optionalBill = billRepository.findById(id);
            if (optionalBill.isPresent()) {
                Bill bill = optionalBill.get();
                switch (flag.toLowerCase()) {
                    case "cleaning":
                        bill.setCleaning(true);
                        bill.setDetail(false);
                        break;
                    case "ready":
                        bill.setReady(true);
                        bill.setCleaning(false);
                        break;
                    case "pickup":
                        bill.setPickup(true);
                        bill.setReady(false);
                        break;
                    case "picked":
                        bill.setPicked(true);
                        bill.setPickup(false);
                        break;
                    default:
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid flag");
                }
                billRepository.save(bill);
                return ResponseEntity.ok(flag + " flag updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bill not found");
            }
        } catch (Exception e) {
            logger.error("Failed to update flag", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update flag");
        }
    }

    @GetMapping("/cleaning")
    public ResponseEntity<List<Bill>> getCleaning() {
        List<Bill> billDetail = billRepository.findByCleaning(true);
        return ResponseEntity.ok(billDetail);
    }

    @GetMapping("/ready")
    public ResponseEntity<List<Bill>> getReady() {
        List<Bill> billDetail = billRepository.findByReady(true);
        return ResponseEntity.ok(billDetail);
    }

    @GetMapping("/pickup")
    public ResponseEntity<List<Bill>> getPickup() {
        List<Bill> billDetail = billRepository.findByPickup(true);
        return ResponseEntity.ok(billDetail);
    }

    @GetMapping("/picked")
    public ResponseEntity<List<Bill>> getPicked() {
        List<Bill> billDetail = billRepository.findByPicked(true);
        return ResponseEntity.ok(billDetail);
    }

    @GetMapping("/paid")
    public ResponseEntity<List<Bill>> getPaidData() {
        List<Bill> paidData = billRepository.findByStatus("paid");
        return ResponseEntity.ok(paidData);
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<Bill>> getUnpaidData() {
        List<Bill> unpaidData = billRepository.findByStatus("unpaid");
        return ResponseEntity.ok(unpaidData);
    }

    @GetMapping("/byDate")
    public ResponseEntity<List<Bill>> getDataByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Bill> dataByDate = billRepository.findByDate(date);
        return ResponseEntity.ok(dataByDate);
    }

    @GetMapping("/records")
    public List<AggregatedBillDTO> getRecords() {
        List<Bill> bills = billRepository.findAll();

        Map<LocalDate, Double> aggregatedData = bills.stream()
                .collect(Collectors.groupingBy(Bill::getDate,
                        Collectors.summingDouble(bill -> {
                            String totalAmountWithTax = bill.getTotalAmountWithTax();
                            try {
                                return totalAmountWithTax != null ? Double.parseDouble(totalAmountWithTax) : 0.0;
                            } catch (NumberFormatException e) {
                                logger.error("Failed to parse total amount with tax for bill id " + bill.getId(), e);
                                return 0.0;
                            }
                        })));

        return aggregatedData.entrySet().stream()
                .map(entry -> new AggregatedBillDTO(entry.getKey(), entry.getValue().toString()))
                .collect(Collectors.toList());
    }

    @GetMapping("/phone-distribution")
    public List<PhoneDistributionDTO> getPhoneDistribution() {
        List<Bill> bills = billRepository.findAll();

        Map<String, Long> phoneCountMap = bills.stream()
                .collect(Collectors.groupingBy(Bill::getPhoneNumber, Collectors.counting()));

        long totalPhoneCount = bills.size();
        logger.info("Total phone count: " + totalPhoneCount);

        List<PhoneDistributionDTO> phoneDistribution = phoneCountMap.entrySet().stream()
                .map(entry -> {
                    double percentage = (entry.getValue() * 100.0) / totalPhoneCount;
                    logger.info("Phone number: " + entry.getKey() + ", Percentage: " + percentage);
                    return new PhoneDistributionDTO(entry.getKey(), percentage);
                })
                .collect(Collectors.toList());

        phoneDistribution.sort((a, b) -> Double.compare(b.getPercentage(), a.getPercentage()));
        List<PhoneDistributionDTO> top5PhoneDistribution = phoneDistribution.size() > 5 ?
                phoneDistribution.subList(0, 5) : phoneDistribution;

        logger.info("Top 5 phone distribution:");
        top5PhoneDistribution.forEach(dto -> logger.info(dto.getPhoneNumber() + ": " + dto.getPercentage()));

        return top5PhoneDistribution;
    }

    @GetMapping("/unpaidByPhoneNumber")
    public ResponseEntity<Map<String, Double>> getUnpaidAmountByPhoneNumber() {
        try {
            List<Bill> unpaidBills = billRepository.findByStatus("unpaid");

            // Calculate the total amount for each phone number
            Map<String, Double> unpaidAmountByPhoneNumber = unpaidBills.stream()
                    .collect(Collectors.groupingBy(Bill::getPhoneNumber,
                            Collectors.summingDouble(bill -> {
                                String totalAmountWithTax = bill.getTotalAmountWithTax();
                                try {
                                    return totalAmountWithTax != null ? Double.parseDouble(totalAmountWithTax) : 0.0;
                                } catch (NumberFormatException e) {
                                    logger.error("Failed to parse total amount with tax for bill id " + bill.getId(), e);
                                    return 0.0;
                                }
                            })));

            return ResponseEntity.ok(unpaidAmountByPhoneNumber);
        } catch (Exception e) {
            logger.error("Failed to get unpaid amount by phone number", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/unpaidamounts/{phoneNumber}")
    public ResponseEntity<List<Bill>> getUnpaidAmountsByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            // Find the unpaid bills for the specified phone number
            List<Bill> unpaidBills = billRepository.findByStatusAndPhoneNumber("unpaid", phoneNumber);

            if (unpaidBills.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(unpaidBills);
            }
        } catch (Exception e) {
            logger.error("Failed to get unpaid amounts by phone number", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/byPhoneNumber/{phoneNumber}")
    public ResponseEntity<List<Bill>> getBillsByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            // Find bills by phone number
            List<Bill> bills = billRepository.findByPhoneNumber(phoneNumber);

            if (bills.isEmpty()) {
                // If no bills found for the given phone number, return not found status
                return ResponseEntity.notFound().build();
            } else {
                // If bills found, return them
                return ResponseEntity.ok(bills);
            }
        } catch (Exception e) {
            // If an error occurs, return internal server error status
            logger.error("Failed to get bills by phone number", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    // New endpoint to get the total amount for unpaid bills on the latest date grouped by phone number
//    @GetMapping("/latestunpaidamount/{phoneNumber}")
//    public ResponseEntity<Double> getLatestUnpaidAmountByPhoneNumber(@PathVariable String phoneNumber) {
//        try {
//            // Find the unpaid bills for the specified phone number
//            List<Bill> unpaidBills = billRepository.findByStatusAndPhoneNumber("unpaid", phoneNumber);
//
//            // Find the latest date among the unpaid bills for the specified phone number
//            Optional<LocalDate> latestDateOpt = unpaidBills.stream()
//                    .map(Bill::getDate)
//                    .max(LocalDate::compareTo);
//
//            if (latestDateOpt.isPresent()) {
//                LocalDate latestDate = latestDateOpt.get();
//
//                // Filter unpaid bills for the latest date and the specified phone number
//                List<Bill> unpaidBillsOnLatestDateForPhoneNumber = unpaidBills.stream()
//                        .filter(bill -> bill.getDate().isEqual(latestDate))
//                        .collect(Collectors.toList());
//
//                // Calculate the total amount for the specified phone number on the latest date
//                Double unpaidAmount = unpaidBillsOnLatestDateForPhoneNumber.stream()
//                        .mapToDouble(bill -> {
//                            String totalAmountWithTax = bill.getTotalAmountWithTax();
//                            try {
//                                return totalAmountWithTax != null ? Double.parseDouble(totalAmountWithTax) : 0.0;
//                            } catch (NumberFormatException e) {
//                                logger.error("Failed to parse total amount with tax for bill id " + bill.getId(), e);
//                                return 0.0;
//                            }
//                        })
//                        .sum();
//
//                return ResponseEntity.ok(unpaidAmount);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            logger.error("Failed to get the latest unpaid amount by phone number", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

}
