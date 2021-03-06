package wordcloudweb

import core.javacore.WordCloudConstants;

class FilterController {

    def index()
    {
        session.subsets = []; // list of Filter objects
        session.corpusSubsets = []; // list of Filter objects
        session.dbFields = WordCloudConstants.META_DB_FIELDS;

        System.out.println("db fields are: " + session.dbFields);

    	// all filter fields will be blank in form
    	def filter = new Filter(name:'', allowedValues:'',
    								sortField:'' );

        render(view:"filters", action:"filters", model: [filter:filter, subsets:session.subsets,
                                                        corpusSubsets:session.corpusSubsets, dbFields:session.dbFields]);
    }

    def filters()
    {
        def curFilter = params.filter;

        [filter:curFilter, subsets:session.subsets,
        corpusSubsets:session.corpusSubsets, dbFields:session.dbFields,
        errorMsg:params.errorMsg];
    }

    /**
    * Add a subset to the list
    **/
    def createSubset()
    {
        def maxNumFilterLvls = 7;
        def newFilter = new Filter();
        newFilter.setName(params.name);
        // TODO: perform error checking on allowedValues list?
        // TODO: ALL OF THE ERROR CHECKING
        for (int i = 1; i <= maxNumFilterLvls; i++)
        {
            def curSortField;
            def curAllowedVals;
            // see if sortfield and allowed vals for this lvl are not null
            // if they are, we are done, because there are no higher levels
            // if not, add sortfield to sortfields, allowedvals to allowedvals,
            // and listify allowedvals as well
            curSortField = params."sortField${i}";
            curAllowedVals = params."allowedValues${i}";

            if (curSortField && (curAllowedVals != null) && (curAllowedVals != ""))
            {
                System.out.println("filter level " + i + " has: ");
                System.out.println("sortfield: " + curSortField);
                System.out.println("allowed vals: " + curAllowedVals);
                newFilter.addSortField(curSortField);
                newFilter.addAllowedValuesString(curAllowedVals);
                newFilter.addAllowedValuesList(this.parseAllowedVals(curAllowedVals));
            } else
            {
                break;
            }
        }

        session.subsets.add(newFilter);
        System.out.println(session.subsets);

        // test print statements
        for (filter in session.subsets)
        {
            System.out.println(filter.name);
        }
        System.out.println("size of subsets:" + session.subsets.size());
        System.out.println("size of corpus subsets:" + session.corpusSubsets.size());

      	def emptyFilter = new Filter(name:'', allowedValues:'',
      								sortField:'' );
      	render(view: "filters", action:"filters",
                  model: [filter:emptyFilter, subsets:session.subsets,
                          corpusSubsets:session.corpusSubsets,
                          dbFields:session.dbFields]);
    }

    private def parseAllowedVals(String allowedVals)
    {
        return Arrays.asList(allowedVals.split("\\s*,\\s*"));
    }

    /**
    * Move an already-created subset to the corpus list
    **/
    def addSubsetToCorpus()
    {
        def nameOfSubset = params.subset; // TODO this var name sucks
        System.out.println("Name of subset: " + nameOfSubset);

        for (filter in session.subsets)
        {
            if (filter.name.equals(nameOfSubset))
            {
                def alreadyInCorpus = false;
                for (corpusFilter in session.corpusSubsets)
                {
                    if (corpusFilter.name.equals(nameOfSubset))
                    {
                        System.out.println("Tried to add " + nameOfSubset
                                           + " to corpus, but it's already in there!");
                        alreadyInCorpus = true;
                        break;
                    }
                }

                if (!alreadyInCorpus)
                {
                    System.out.println("Adding subset " + nameOfSubset + " to corpus.");
                    session.corpusSubsets.add(filter);
                    break;
                }
            }
        }

        def emptyFilter = new Filter(name:'', allowedValues:'',
                                    sortField:'' );
        render(view: "filters", action:"filters",
                model: [filter:emptyFilter, subsets:session.subsets,
                        corpusSubsets:session.corpusSubsets,
                        dbFields:session.dbFields]);
    }

    def removeSubsetFromCorpus()
    {
        def nameOfSubset = params.corpusFilter; // TODO this var name sucks
        System.out.println("We're gonna remove subset "  + nameOfSubset
                            + " from the corpus!");

        def idxToRemove = -1;

        for (int i = 0; i < session.corpusSubsets.size(); ++i)
        {
            def curSubset = session.corpusSubsets[i];
            if (curSubset.name.equals(nameOfSubset))
            {
                idxToRemove = i;
                break;
            }
        }

        if (idxToRemove != -1)
        {
            session.corpusSubsets.remove(idxToRemove);
        } else
        {
            System.out.println("Didn't find subset " + nameOfSubset + " in the corpus!");
        }

        def emptyFilter = new Filter(name:'', allowedValues:'',
                                    sortField:'' );
        render(view: "filters", action:"filters",
                model: [filter:emptyFilter, subsets:session.subsets,
                        corpusSubsets:session.corpusSubsets,
                        dbFields:session.dbFields]);
    }

    def addAllSubsetsToCorpus()
    {
        System.out.println("We're gonna add all subsets to the corpus.");

        session.corpusSubsets.clear();
        for (filter in session.subsets)
        {
            session.corpusSubsets.add(filter);
        }

        def emptyFilter = new Filter(name:'', allowedValues:'',
                                    sortField:'' );
        render(view: "filters", action:"filters",
                model: [filter:emptyFilter, subsets:session.subsets,
                        corpusSubsets:session.corpusSubsets,
                        dbFields:session.dbFields]);
    }

    def removeAllSubsetsFromCorpus()
    {
        System.out.println("We're gonna remove all subsets from the corpus.");

        session.corpusSubsets.clear();

        def emptyFilter = new Filter(name:'', allowedValues:'',
                                    sortField:'' );
        render(view: "filters", action:"filters",
                model: [filter:emptyFilter, subsets:session.subsets,
                        corpusSubsets:session.corpusSubsets,
                        dbFields:session.dbFields]);
    }

    def createWordCloud()
    {
        def nameOfSubset = params.subset;
        def corpusFilters = session.corpusSubsets;

        System.out.println("name of subset to use for word cloud: " + nameOfSubset);
        System.out.println("number of corpus filters: " + corpusFilters.size());
        for (filter in corpusFilters)
        {
            System.out.println("name of corpus subset to use for word cloud: " + filter.name);
        }

        // get subset filter and corpus filter objects to pass
        def subsetFilter;
        for (filter in session.subsets)
        {
            if (filter.name.equals(nameOfSubset))
            {
                subsetFilter = filter;
                break;
            }
        }

        if (subsetFilter == null)
        {
            System.out.println("No subset filter selected... oh no.");

            def emptyFilter = new Filter(name:'', allowedValues:'',
                                    sortField:'' );
            render(view: "filters", action:"filters",
                model: [filter:emptyFilter, subsets:session.subsets,
                        corpusSubsets:session.corpusSubsets,
                        dbFields:session.dbFields,
                        errorMsg:"You must select a created subset for the word cloud!"]);
        } else
        {
            System.out.println("subset filter has: ");
            System.out.println("name: " + subsetFilter.getName());

            session.subsetFilter = subsetFilter;
            session.corpusFilters = corpusFilters;
            redirect(controller: "Demo",
                    action: "createCloud");
        }
    }
}
