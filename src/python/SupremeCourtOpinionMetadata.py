from src.core.python.Metadata import Metadata

class SupremeCourtOpinionMetadata(Metadata):
    '''
    SupremeCourtOpinionMetadata Class for Word Cloud Project
    
    Daniel Klein
    Computer-Based Honors Program
    The University of Alabama
    10.16.2013
    
    This specializes the generic Metadata class to apply to Supreme Court 
    opinions. Only the the constructor is overridden.
    '''


    def __init__(self):
        '''
        This overrides the original dummy constructor in the Metadata class.
        '''
        self.field_names = (["case_title", "case_num", "case_us_cite",
                             "case_supreme_court_cite", "case_lawyers_ed_cite",
                             "case_lexis_cite", "case_full_cite", "case_dates", 
                             "case_disposition", "opinion_author", 
                             "opinion_type"])
        self.case_title = ""
        self.case_num = ""
        self.case_us_cite = ""
        self.case_supreme_court_cite = ""
        self.case_lawyers_ed_cite = ""
        self.case_lexis_cite = ""
        self.case_full_cite = ""
        self.case_dates = ""
        self.case_disposition = ""
        self.opinion_author = ""
        self.opinion_type = ""
        
        
    def build_output_string(self):
        '''
        Builds string to be used for displaying metadata in GUI.
        '''
        pass