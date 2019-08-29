package io.github.edmm.plugins.chef;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.visitor.VisitorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChefLifecycle extends AbstractLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChefLifecycle.class);
    public static final String COOKBOOKS_FOLDER = "cookbooks";
    public static final String POLICIES_FOLDER = "policies";
    public static final String POLICY_FILENAME = "Policyfile.rb";
    public static final String COOKBOOK_RECIPES_FOLDER = "recipes";
    public static final String COOKBOOK_FILES_FOLDER = "files";
    public static final String COOKBOOK_ATTRIBUTES_FOLDER = "attributes";
    public static final String COOKBOOK_DEFAULT_RECIPE_FILENAME = "default.rb";
    public static final String COOKBOOK_METADATA_FILENAME = "metadata.rb";
    public static final String COOKBOOK_CHEFIGNORE_FILENAME = "chefignore";

    private final TransformationContext context;

    public ChefLifecycle(TransformationContext context) {
        this.context = context;
    }

    @Override
    public void prepare() {
        LOGGER.info("Prepare transformation for Chef...");
    }

    @Override
    public void transform() {
        LOGGER.info("Begin transformation to Chef...");
        ChefTransformer transformer = new ChefTransformer(context);
        transformer.populateChefRepository();
        LOGGER.info("Transformation to Chef successful");
    }

    @Override
    public void cleanup() {
        LOGGER.info("Cleanup transformation leftovers...");
    }
}
