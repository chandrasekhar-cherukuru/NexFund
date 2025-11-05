import React, { useState, useEffect } from 'react';
import { useParams, useSearchParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { Users, Calendar, IndianRupee, Download, Search, Filter, CheckCircle, Clock, Mail, CreditCard, Check, AlertCircle, X } from 'lucide-react';
import Navbar from '../Layout/Navbar';
import toast from 'react-hot-toast';
import * as api from '../../services/api.js';
import * as XLSX from 'xlsx';


const VerifyParticipants = () => {
  const { user } = useAuth();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  
  const [participants, setParticipants] = useState([]);
  const [filteredParticipants, setFilteredParticipants] = useState([]);
  const [finalVerifications, setFinalVerifications] = useState(new Set());
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('all');
  const [verificationFilter, setVerificationFilter] = useState('all');
  const [stats, setStats] = useState({ 
    totalParticipants: 0, 
    totalAmount: 0, 
    pendingCount: 0, 
    verifiedCount: 0 
  });

  const fundraiserId = searchParams.get('fundraiserId');

  useEffect(() => {
    if (!user) {
      toast.error('Please login to access this page');
      navigate('/');
      return;
    }
    
    loadParticipants();
  }, [user, navigate, fundraiserId]);

  useEffect(() => {
    filterParticipants();
  }, [participants, searchTerm, filterType, verificationFilter, finalVerifications]);

  useEffect(() => {
    updateStats();
  }, [participants, finalVerifications]);

  const updateStats = () => {
    const totalParticipants = participants.length;
    const totalAmount = participants.reduce((sum, p) => sum + parseFloat(p.amountPaid || 0), 0);
    const verifiedCount = finalVerifications.size;
    const pendingCount = totalParticipants - verifiedCount;
    
    setStats({ totalParticipants, totalAmount, pendingCount, verifiedCount });
    console.log('📊 Stats updated:', { totalParticipants, verifiedCount, pendingCount });
  };

  const loadParticipants = async () => {
    try {
      setIsLoading(true);
      
      let response;
      console.log('🔄 Loading participants...');
      
      if (fundraiserId) {
        console.log('📋 Loading participants for specific fundraiser');
        response = await api.getParticipantsByFundraiser(fundraiserId);
      } else {
        console.log('📋 Loading all participants');
        response = await api.getMyFundraiserParticipants();
      }
      
      console.log('✅ Response data:', response.data);
      
      setParticipants(response.data || []);
      
      await loadFinalVerifications(response.data || []);
      
      console.log('✅ Participants loaded:', (response.data || []).length);
    } catch (error) {
      console.error('❌ Failed to load participants:', error);
      
      if (error.response?.status === 401) {
        toast.error('Authentication failed. Please login again.');
        localStorage.clear();
        navigate('/');
      } else if (error.response?.status === 403) {
        toast.error('Access denied. Check your permissions.');
      } else {
        toast.error(`Failed to load participants: ${error.response?.data?.message || error.message}`);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const loadFinalVerifications = async (participantList) => {
    try {
      console.log('🔄 Loading final verifications...');
      
      const verifiedSet = new Set();
      
      for (const participant of participantList) {
        try {
          const response = await api.checkFinalVerification(participant.id);
          console.log(`🔍 Checking ${participant.id}:`, response.data);
          if (response.data && response.data.isFinallyVerified) {
            verifiedSet.add(participant.id);
            console.log('✅ Found verified participant:', participant.id);
          }
        } catch (error) {
          console.log(`⚠️ Participant ${participant.id} not verified or error:`, error.message);
        }
      }
      
      console.log('✅ Final verified set:', Array.from(verifiedSet));
      setFinalVerifications(verifiedSet);
      
    } catch (error) {
      console.error('❌ Failed to load final verifications:', error);
      setFinalVerifications(new Set());
    }
  };

  const filterParticipants = () => {
    let filtered = participants;
    
    if (searchTerm.trim()) {
      const term = searchTerm.toLowerCase();
      filtered = filtered.filter(p => 
        p.participantName?.toLowerCase().includes(term) ||
        p.utrNumber?.includes(term) ||
        p.fundraiserTitle?.toLowerCase().includes(term) ||
        (p.email && p.email.toLowerCase().includes(term))
      );
    }
    
    if (filterType !== 'all') {
      filtered = filtered.filter(p => p.fundraiserType === filterType);
    }
    
    if (verificationFilter !== 'all') {
      filtered = filtered.filter(p => {
        if (verificationFilter === 'verified') {
          return finalVerifications.has(p.id);
        } else if (verificationFilter === 'pending') {
          return !finalVerifications.has(p.id);
        }
        return true;
      });
    }
    
    setFilteredParticipants(filtered);
  };

  const handleFinalVerification = async (participantId, participantName) => {
    if (!window.confirm(`Are you sure you want to FINALLY VERIFY "${participantName}"?`)) {
      return;
    }

    try {
      console.log('🔄 Attempting backend verification for:', participantId);
      
      const response = await api.finallyVerifyParticipant(participantId, {
        notes: `Finally verified by ${user?.username || user?.email} on ${new Date().toISOString()}`
      });
      
      console.log('✅ Backend verification successful:', response.data);
      
      setFinalVerifications(prev => {
        const newSet = new Set(prev);
        newSet.add(participantId);
        console.log('✅ Updated verified set:', Array.from(newSet));
        return newSet;
      });
      
      toast.success(`${participantName} has been finally verified successfully!`);
      
      setTimeout(async () => {
        try {
          const checkResponse = await api.checkFinalVerification(participantId);
          if (checkResponse.data.isFinallyVerified) {
            console.log('✅ Confirmed: Participant verified in database!');
          }
        } catch (err) {
          console.log('⚠️ Could not verify database save:', err);
        }
      }, 1500);
      
    } catch (error) {
      console.error('❌ Backend verification failed:', error);
      
      let errorMessage = 'Failed to verify participant';
      
      if (error.response?.status === 400 && error.response?.data?.message) {
        errorMessage = error.response.data.message;
        toast.error(errorMessage);
      } else if (error.response?.status === 403) {
        errorMessage = 'Access denied. You can only verify participants from your own fundraisers.';
        toast.error(errorMessage);
      } else if (error.response?.status === 401) {
        errorMessage = 'Authentication failed. Please login again.';
        toast.error(errorMessage);
        localStorage.clear();
        navigate('/');
        return;
      } else {
        toast.error(errorMessage);
      }
    }
  };

  const handleDownloadReport = () => {
    const finallyVerifiedParticipants = filteredParticipants.filter(p => finalVerifications.has(p.id));
    
    if (finallyVerifiedParticipants.length === 0) {
      toast.error('No finally verified participants to download.');
      return;
    }

    downloadExcel(finallyVerifiedParticipants);
  };

  const downloadExcel = (data) => {
    try {
      const workbook = XLSX.utils.book_new();

      // Sort data alphabetically
      const sortedData = [...data].sort((a, b) => 
        (a.participantName || '').localeCompare(b.participantName || '')
      );

      const eventName = sortedData.length > 0 ? sortedData[0].fundraiserTitle : 'Report';
      const creatorName = user?.username || user?.email || 'Unknown';

      // === SHEET 1: REPORT ===
      const reportData = [];
      
      // Header Section
      reportData.push([eventName]);
      reportData.push([`Created by: ${creatorName}`]);
      reportData.push([`Generated on: ${new Date().toLocaleString()}`]);
      reportData.push([]);
      
      // Column Headers
      reportData.push(['S.No', 'Participant Name', 'UTR Number', 'Email', 'Amount (₹)', 'Verified']);
      
      // Add participant rows with proper formatting
      sortedData.forEach((p, index) => {
        reportData.push([
          index + 1,
          p.participantName || '',
          p.utrNumber || '',
          p.email || 'N/A',
          parseFloat(p.amountPaid || 0),
          finalVerifications.has(p.id) ? 'Yes' : 'No'
        ]);
      });

      // Summary Section
      reportData.push([]);
      reportData.push(['SUMMARY']);
      reportData.push(['Total Participants', sortedData.length]);
      
      const totalAmount = sortedData.reduce((sum, p) => sum + parseFloat(p.amountPaid || 0), 0);
      reportData.push(['Total Amount Collected', totalAmount]);
      reportData.push(['Average per Participant', (totalAmount / sortedData.length).toFixed(2)]);

      const reportSheet = XLSX.utils.aoa_to_sheet(reportData);
      
      // Set column widths
      reportSheet['!cols'] = [
        { wch: 5 },      // S.No
        { wch: 25 },     // Participant Name
        { wch: 18 },     // UTR Number
        { wch: 32 },     // Email
        { wch: 15 },     // Amount
        { wch: 10 }      // Verified
      ];

      XLSX.utils.book_append_sheet(workbook, reportSheet, 'Report');

      // === SHEET 2: SUMMARY ONLY ===
      const summaryData = [
        ['Event Report Summary'],
        [],
        ['Event Name', eventName],
        ['Creator', creatorName],
        ['Generated', new Date().toLocaleString()],
        [],
        ['Total Participants', sortedData.length],
        ['Total Amount', totalAmount],
        ['Average Amount', (totalAmount / sortedData.length).toFixed(2)],
      ];

      const summarySheet = XLSX.utils.aoa_to_sheet(summaryData);
      summarySheet['!cols'] = [{ wch: 25 }, { wch: 40 }];
      XLSX.utils.book_append_sheet(workbook, summarySheet, 'Summary');

      const fileName = `${eventName.replace(/\s+/g, '_')}_report_${new Date().toISOString().split('T')[0]}.xlsx`;
      XLSX.writeFile(workbook, fileName);
      toast.success('Report downloaded successfully! ✅');
      console.log('✅ Excel downloaded successfully');
    } catch (error) {
      console.error('❌ Excel download error:', error);
      toast.error('Failed to download report');
    }
  };

  const getTypeColor = (type) => {
    switch (type) {
      case 'event':
        return 'text-blue-600 bg-blue-100 dark:text-blue-400 dark:bg-blue-900/30';
      case 'donation':
        return 'text-red-600 bg-red-100 dark:text-red-400 dark:bg-red-900/30';
      case 'gift':
        return 'text-purple-600 bg-purple-100 dark:text-purple-400 dark:bg-purple-900/30';
      default:
        return 'text-gray-600 bg-gray-100 dark:text-gray-400 dark:bg-gray-700';
    }
  };

  const getVerificationStatusBadge = (participantId) => {
    if (finalVerifications.has(participantId)) {
      return (
        <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-green-100 dark:bg-green-900/30 text-green-800 dark:text-green-300">
          <CheckCircle className="h-4 w-4 mr-1" />
          Finally Verified
        </span>
      );
    } else {
      return (
        <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-yellow-100 dark:bg-yellow-900/30 text-yellow-800 dark:text-yellow-300">
          <Clock className="h-4 w-4 mr-1" />
          Pending Final Verification
        </span>
      );
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
        <Navbar />
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 dark:border-blue-400"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-200">
      <Navbar />
      
      <div className="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">Finally Verified Participants</h1>
          <p className="text-gray-600 dark:text-gray-400">
            {fundraiserId ? 'Participants for selected fundraiser' : 'All verified participants from your fundraisers'}
          </p>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-500 dark:text-gray-400">Total Participants</p>
                <p className="text-3xl font-bold text-gray-900 dark:text-white mt-2">{stats.totalParticipants}</p>
              </div>
              <div className="p-3 bg-blue-100 dark:bg-blue-900/30 rounded-lg">
                <Users className="h-6 w-6 text-blue-600 dark:text-blue-400" />
              </div>
            </div>
          </div>

          <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-500 dark:text-gray-400">Finally Verified</p>
                <p className="text-3xl font-bold text-green-600 dark:text-green-400 mt-2">{stats.verifiedCount}</p>
              </div>
              <div className="p-3 bg-green-100 dark:bg-green-900/30 rounded-lg">
                <CheckCircle className="h-6 w-6 text-green-600 dark:text-green-400" />
              </div>
            </div>
          </div>

          <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-500 dark:text-gray-400">Pending</p>
                <p className="text-3xl font-bold text-yellow-600 dark:text-yellow-400 mt-2">{stats.pendingCount}</p>
              </div>
              <div className="p-3 bg-yellow-100 dark:bg-yellow-900/30 rounded-lg">
                <AlertCircle className="h-6 w-6 text-yellow-600 dark:text-yellow-400" />
              </div>
            </div>
          </div>

          <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-500 dark:text-gray-400">Total Amount</p>
                <p className="text-3xl font-bold text-purple-600 dark:text-purple-400 mt-2">₹{stats.totalAmount.toLocaleString()}</p>
              </div>
              <div className="p-3 bg-purple-100 dark:bg-purple-900/30 rounded-lg">
                <IndianRupee className="h-6 w-6 text-purple-600 dark:text-purple-400" />
              </div>
            </div>
          </div>
        </div>

        {/* Filters and Search */}
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 mb-8">
          <div className="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
            <div className="flex flex-col md:flex-row md:items-center space-y-4 md:space-y-0 md:space-x-4">
              {/* Search */}
              <div className="relative">
                <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400 dark:text-gray-500" />
                <input
                  type="text"
                  placeholder="Search by name, UTR, or fundraiser..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10 pr-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 dark:focus:ring-blue-400 dark:focus:border-blue-400 w-full md:w-64 bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400"
                />
              </div>

              {/* Type Filter */}
              <div className="relative">
                <Filter className="absolute left-3 top-3 h-4 w-4 text-gray-400 dark:text-gray-500" />
                <select
                  value={filterType}
                  onChange={(e) => setFilterType(e.target.value)}
                  className="pl-10 pr-8 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 dark:focus:ring-blue-400 dark:focus:border-blue-400 bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
                >
                  <option value="all">All Types</option>
                  <option value="event">Events</option>
                  <option value="donation">Donations</option>
                  <option value="gift">Gifts</option>
                </select>
              </div>

              {/* Verification Status Filter */}
              <div className="relative">
                <CheckCircle className="absolute left-3 top-3 h-4 w-4 text-gray-400 dark:text-gray-500" />
                <select
                  value={verificationFilter}
                  onChange={(e) => setVerificationFilter(e.target.value)}
                  className="pl-10 pr-8 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 dark:focus:ring-blue-400 dark:focus:border-blue-400 bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
                >
                  <option value="all">All Status</option>
                  <option value="pending">Pending Final Verification</option>
                  <option value="verified">Finally Verified</option>
                </select>
              </div>
            </div>

            {/* Download Button */}
            <button
              onClick={handleDownloadReport}
              disabled={stats.verifiedCount === 0}
              className="flex items-center space-x-2 bg-green-600 hover:bg-green-700 dark:bg-green-500 dark:hover:bg-green-600 text-white px-4 py-2 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed font-medium whitespace-nowrap"
            >
              <Download className="h-4 w-4" />
              <span>Download Report</span>
            </button>
          </div>
        </div>

        {/* Participants Table */}
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
          <div className="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
            <h2 className="text-xl font-semibold text-gray-900 dark:text-white">
              Participants ({filteredParticipants.length})
            </h2>
          </div>
          
          {filteredParticipants.length === 0 ? (
            <div className="p-8 text-center">
              <Users className="h-12 w-12 text-gray-300 dark:text-gray-600 mx-auto mb-4" />
              <h3 className="text-lg font-medium text-gray-500 dark:text-gray-400 mb-2">
                {searchTerm || filterType !== 'all' || verificationFilter !== 'all' ? 'No participants match your filters' : 'No participants yet'}
              </h3>
              <p className="text-gray-400 dark:text-gray-500">
                {searchTerm || filterType !== 'all' || verificationFilter !== 'all'
                  ? 'Try adjusting your search or filter criteria' 
                  : 'Participants will appear here once they complete payment verification'
                }
              </p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
                <thead className="bg-gray-50 dark:bg-gray-700">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                      Participant
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                      Transaction
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                      Fundraiser
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                      Amount
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                      Status
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                      Action
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
                  {filteredParticipants.map((participant) => (
                    <tr key={participant.id} className="hover:bg-gray-50 dark:hover:bg-gray-700">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center">
                          <div className="flex-shrink-0 h-10 w-10">
                            <div className="h-10 w-10 rounded-full bg-blue-100 dark:bg-blue-900 flex items-center justify-center">
                              <Users className="h-5 w-5 text-blue-600 dark:text-blue-400" />
                            </div>
                          </div>
                          <div className="ml-4">
                            <div className="text-sm font-medium text-gray-900 dark:text-white">{participant.participantName}</div>
                            {participant.email && (
                              <div className="text-sm text-gray-500 dark:text-gray-400 flex items-center">
                                <Mail className="h-3 w-3 mr-1" />
                                {participant.email}
                              </div>
                            )}
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center">
                          <CreditCard className="h-4 w-4 text-gray-400 dark:text-gray-500 mr-2" />
                          <div>
                            <div className="text-sm font-medium text-gray-900 dark:text-white">UTR: {participant.utrNumber}</div>
                            <div className="text-sm text-gray-500 dark:text-gray-400">Payment verified</div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div>
                          <div className="text-sm font-medium text-gray-900 dark:text-white">{participant.fundraiserTitle}</div>
                          <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getTypeColor(participant.fundraiserType)}`}>
                            <span className="capitalize">{participant.fundraiserType}</span>
                          </span>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center">
                          <IndianRupee className="h-4 w-4 text-green-600 dark:text-green-400 mr-1" />
                          <span className="text-sm font-medium text-gray-900 dark:text-white">
                            {parseFloat(participant.amountPaid || 0).toLocaleString()}
                          </span>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {getVerificationStatusBadge(participant.id)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {finalVerifications.has(participant.id) ? (
                          <span className="inline-flex items-center px-3 py-1.5 text-xs font-medium text-green-800 dark:text-green-300 bg-green-100 dark:bg-green-900/30 rounded-lg">
                            <CheckCircle className="h-3 w-3 mr-1" />
                            Finally Verified
                          </span>
                        ) : (
                          <button
                            onClick={() => handleFinalVerification(participant.id, participant.participantName)}
                            className="inline-flex items-center px-3 py-1.5 bg-orange-600 hover:bg-orange-700 dark:bg-orange-500 dark:hover:bg-orange-600 text-white text-xs font-medium rounded-lg transition-colors"
                          >
                            <Check className="h-3 w-3 mr-1" />
                            Final Verify
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default VerifyParticipants;
