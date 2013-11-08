import re
from string import punctuation
from Document import Document
from nltk.stem.porter import PorterStemmer
from nltk.stem.lancaster import LancasterStemmer
from nltk.corpus import stopwords

class DocumentStorage(Document):
    '''
    Daniel Klein
    Computer-Based Honors Program
    The University of Alabama
    10.27.2013
    
    This guy is basically just a Document object along with a place
    to store the list of terms in the document and each of their 
    frequencies.
    '''
    
    def __init__(self, doc_metadata, doc_text, output_filename):
        Document.__init__(self, doc_metadata, doc_text, output_filename)
        self.identifier = self.doc_metadata.opinion_author + "_" \
                            + self.doc_metadata.case_lexis_cite
        # remove footnotes?
        # remove punctuation from doc_text
        #self.doc_text = self.remove_footnotes(self.doc_text)
        self.doc_text = re.sub('[%s]' % re.escape(punctuation), ' ', self.doc_text)
        self.doc_text = re.sub('[\d]', '', self.doc_text)
        self.doc_text = re.sub('\s.\s', ' ', self.doc_text)
        # split_text contains the full text of the document
        self.split_text = self.create_split_text(self.doc_text)
        # term_list is a list of unique terms in the document along with
        # each term's term frequency and tf_idf metric.
        self.term_list = self.build_term_list()
        
        
    def create_split_text(self, text):
        '''
        Turns the text of the document into a list of terms, removing
        stop words and stemming words when necessary.
        '''
        split_text = [word.lower() for word in text.split()]
        split_text = self.remove_stop_words(split_text)
        split_text = self.stem_text(split_text)
        return split_text
        
        
    def build_term_list(self):
        '''
        Build term list of form 
        {term1: {'tf':0, 'tf_idf':0}, term2:{'tf':0, 'tf_idf':0}, ... , 
        termn:{'tf':0, 'tf_idf':0}}
        
        Term frequency is calculated when each term is added to the
        list, but tf_idf is not.
        '''
        self.term_list = {}
        for term in self.split_text:
            if not term in self.term_list:
                self.term_list[term] = {"tf":None, "tf_idf":None}
                self.term_list[term]['tf'] = self.calculate_term_frequency(term)            
        # test output
        #print self.term_list
        # /test output
        return self.term_list
    
    
    def remove_footnotes(self, text):
        '''
        Removes footnotes sections from the text of the document.
        '''
        pass
    
    
    def remove_stop_words(self, word_list):
        '''
        Removes stop words from word_list.
        '''
        filtered_text = ([word for word in word_list if not word in
                         stopwords.words('english')])
        # test output
        #print word_list
        #print filtered_text
        # \ test output
        return filtered_text
    
    
    def stem_text(self, word_list):
        '''
        Stems the appropriate words in the given word_list.
        '''
        stemmed_list = []
        stemmer = PorterStemmer()
        #stemmer = LancasterStemmer()
        for word in word_list:
            stemmed_list.append(stemmer.stem(word))
        # test output
        #print stemmed_list
        # /test output
        return stemmed_list
    
    
    def calculate_term_frequency(self, term):
        '''
        Given a term and a doc, calculates term's relative frequency
        in that doc, ie
        (# times term appears in doc) / (# total terms in doc)
        '''
        term_freq = self.split_text.count(term) / float(len(self.term_list))
        return term_freq
    
    
    def calc_tfidf(self, term, doc_freq):
        '''
        Given a term and its relative doc frequency, calculates the tf-idf 
        for the term in the document.
        '''
        if term in self.term_list:
            term_freq = self.term_list[term]['tf']
        else:
            term_freq = 0
        tf_idf = term_freq / float(doc_freq)
        # saves the tf_idf in the term_list... is this really necessary?
        if term in self.term_list:
            self.term_list[term]['tf_idf'] = tf_idf
        #
        return tf_idf

        