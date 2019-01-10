package com.salesmanager.test.shop.controller.category.rest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.salesmanager.shop.application.ShopApplication;
import com.salesmanager.shop.model.catalog.category.Category;
import com.salesmanager.shop.model.catalog.category.CategoryDescription;
import com.salesmanager.shop.model.catalog.category.PersistableCategory;
import com.salesmanager.shop.model.catalog.category.ReadableCategory;

@SpringBootTest(classes = ShopApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class CategoryManagementAPIIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;


    private HttpHeaders getHeader() {
        final HttpHeaders headers = new HttpHeaders();
        final MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
        // MediaType.APPLICATION_JSON //for application/json
        headers.setContentType(mediaType);
        // Basic Authentication
        final String authorisation = "admin" + ":" + "password";
        final byte[] encodedAuthorisation = Base64.encode(authorisation.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        return headers;
    }

    /**
     * Read - GET a category by id
     *
     * @throws Exception
     */
    @Test
    public void getCategory() throws Exception {
        final HttpEntity<String> httpEntity = new HttpEntity<>(getHeader());

        final ResponseEntity<List> response = testRestTemplate.exchange(String.format("/services/public/category/DEFAULT/en/?lang=en"), HttpMethod.GET,
                httpEntity, List.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception(response.toString());
        } else {
            final List<ReadableCategory> categories = response.getBody();
            assertNotNull(categories);
        }
    }

    /**
     * Creates - POST a category for a given store
     *
     * @throws Exception
     */

    @Test
    public void postCategory() throws Exception {

        final PersistableCategory newCategory = new PersistableCategory();
        newCategory.setCode("javascript");
        newCategory.setSortOrder(1);
        newCategory.setVisible(true);
        newCategory.setDepth(4);

        final Category parent = new Category();

        newCategory.setParent(parent);

        final CategoryDescription description = new CategoryDescription();
        description.setLanguage("en");
        description.setName("Javascript");
        description.setFriendlyUrl("javascript");
        description.setTitle("Javascript");

        final List<CategoryDescription> descriptions = new ArrayList<>();
        descriptions.add(description);

        newCategory.setDescriptions(descriptions);

        final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        final String json = writer.writeValueAsString(newCategory);

        System.out.println(json);

        final HttpEntity<String> entity = new HttpEntity<>(json, getHeader());

        final ResponseEntity response = testRestTemplate.postForEntity("/services/private/DEFAULT/category", entity, PersistableCategory.class);

        final PersistableCategory cat = (PersistableCategory) response.getBody();

        assertNotNull(cat.getId());

        final ResponseEntity<List> listResponse = testRestTemplate.exchange(String.format("/services/public/category/DEFAULT/en/?lang=en"), HttpMethod.GET,
                new HttpEntity<>(getHeader()), List.class);
        assertTrue(!listResponse.getBody().isEmpty());

    }

    @Test
    public void postComplexCategory() throws Exception {

        /** Dining room **/
        final PersistableCategory dining = new PersistableCategory();
        dining.setCode("dining room");
        dining.setSortOrder(0);
        dining.setVisible(true);

        CategoryDescription endescription = new CategoryDescription();
        endescription.setLanguage("en");
        endescription.setName("Dining room");
        endescription.setFriendlyUrl("dining-room");
        endescription.setTitle("Dining room");

        CategoryDescription frdescription = new CategoryDescription();
        frdescription.setLanguage("fr");
        frdescription.setName("Salle à manger");
        frdescription.setFriendlyUrl("salle-a-manger");
        frdescription.setTitle("Salle à manger");

        List<CategoryDescription> descriptions = new ArrayList<>();
        descriptions.add(endescription);
        descriptions.add(frdescription);

        dining.setDescriptions(descriptions);

        final Category diningParent = new Category();
        diningParent.setCode(dining.getCode());

        /** armoire **/
        final PersistableCategory armoire = new PersistableCategory();
        armoire.setCode("armoire");
        armoire.setSortOrder(1);
        armoire.setVisible(true);

        armoire.setParent(diningParent);

        endescription = new CategoryDescription();
        endescription.setLanguage("en");
        endescription.setName("Armoires");
        endescription.setFriendlyUrl("armoires");
        endescription.setTitle("Armoires");

        frdescription = new CategoryDescription();
        frdescription.setLanguage("fr");
        frdescription.setName("Armoire");
        frdescription.setFriendlyUrl("armoires");
        frdescription.setTitle("Armoires");

        descriptions = new ArrayList<>();
        descriptions.add(endescription);
        descriptions.add(frdescription);

        armoire.setDescriptions(descriptions);
        dining.getChildren().add(armoire);

        /** benches **/
        final PersistableCategory bench = new PersistableCategory();
        bench.setCode("bench");
        bench.setSortOrder(4);
        bench.setVisible(true);

        bench.setParent(diningParent);

        endescription = new CategoryDescription();
        endescription.setLanguage("en");
        endescription.setName("Benches");
        endescription.setFriendlyUrl("benches");
        endescription.setTitle("Benches");

        frdescription = new CategoryDescription();
        frdescription.setLanguage("fr");
        frdescription.setName("Bancs");
        frdescription.setFriendlyUrl("bancs");
        frdescription.setTitle("Bancs");

        descriptions = new ArrayList<>();
        descriptions.add(endescription);
        descriptions.add(frdescription);

        bench.setDescriptions(descriptions);
        dining.getChildren().add(bench);

        /** Living room **/
        final PersistableCategory living = new PersistableCategory();
        living.setCode("livingroom");
        living.setSortOrder(2);
        living.setVisible(true);

        endescription = new CategoryDescription();
        endescription.setLanguage("en");
        endescription.setName("Living room");
        endescription.setFriendlyUrl("living-room");
        endescription.setTitle("Living room");

        frdescription = new CategoryDescription();
        frdescription.setLanguage("fr");
        frdescription.setName("Salon");
        frdescription.setFriendlyUrl("salon");
        frdescription.setTitle("Salon");

        descriptions = new ArrayList<>();
        descriptions.add(endescription);
        descriptions.add(frdescription);

        living.setDescriptions(descriptions);

        /** lounge **/

        final PersistableCategory lounge = new PersistableCategory();
        lounge.setCode("lounge");
        lounge.setSortOrder(3);
        lounge.setVisible(true);

        final Category livingParent = living;
        lounge.setParent(livingParent);

        endescription = new CategoryDescription();
        endescription.setLanguage("en");
        endescription.setName("Lounge");
        endescription.setFriendlyUrl("lounge");
        endescription.setTitle("Lounge");

        frdescription = new CategoryDescription();
        frdescription.setLanguage("fr");
        frdescription.setName("Divan");
        frdescription.setFriendlyUrl("divan");
        frdescription.setTitle("Divan");

        descriptions = new ArrayList<>();
        descriptions.add(endescription);
        descriptions.add(frdescription);

        lounge.setDescriptions(descriptions);
        living.getChildren().add(lounge);

        final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        final String json = writer.writeValueAsString(dining);

        System.out.println(json);

        final HttpEntity<String> entity = new HttpEntity<>(json, getHeader());

        final int sizeBefore = testRestTemplate.exchange(String.format("/services/public/category/DEFAULT/en/?lang=en"), HttpMethod.GET,
                new HttpEntity<>(getHeader()), List.class).getBody().size();

        final ResponseEntity response = testRestTemplate.postForEntity("/services/private/DEFAULT/category", entity, PersistableCategory.class);

        final PersistableCategory cat = (PersistableCategory) response.getBody();
        assertNotNull(cat.getId());

        final int sizeAfter = testRestTemplate.exchange(String.format("/services/public/category/DEFAULT/en/?lang=en"), HttpMethod.GET,
                new HttpEntity<>(getHeader()), List.class).getBody().size();
        assertTrue(sizeAfter > sizeBefore);

    }

    @Test
    public void deleteCategory() throws Exception {

        final HttpEntity<String> httpEntity = new HttpEntity<>(getHeader());

        testRestTemplate.exchange("/services/DEFAULT/category/100", HttpMethod.DELETE, httpEntity, Category.class);
        System.out.println("Category id 100 Deleted.");
    }

}
