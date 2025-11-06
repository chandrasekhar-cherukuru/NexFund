import React, { useState } from 'react';
import { Star, Send, X } from 'lucide-react';
import toast from 'react-hot-toast';

const FeedbackModal = ({ isOpen, onClose }) => {
  const [feedbackData, setFeedbackData] = useState({
    name: '',
    email: '',
    message: '',
    type: 'event',
    rating: 5,
  });

  const [isSubmittingFeedback, setIsSubmittingFeedback] = useState(false);

  if (!isOpen) return null;

  const handleFeedbackChange = (e) => {
    const { name, value } = e.target;
    setFeedbackData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFeedbackRating = (rating) => {
    setFeedbackData((prev) => ({ ...prev, rating }));
  };

  const handleFeedbackSubmit = async (e) => {
    e.preventDefault();
    setIsSubmittingFeedback(true);

    try {
      const response = await fetch('http://localhost:8080/feedback', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(feedbackData),
      });

      const data = await response.json();

      if (response.ok) {
        toast.success('Thank you! Your feedback has been submitted! 🙏');
        setFeedbackData({ name: '', email: '', message: '', type: 'event', rating: 5 });
        onClose();
        
        // Refresh approved feedbacks in Landing
        window.dispatchEvent(new Event('feedbackSubmitted'));
      } else {
        toast.error(data.error || 'Failed to submit feedback');
      }
    } catch (error) {
      console.error('❌ Error submitting feedback:', error);
      toast.error('Error submitting feedback');
    } finally {
      setIsSubmittingFeedback(false);
    }
  };

  const handleSkipFeedback = () => {
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white dark:bg-gray-800 rounded-xl shadow-2xl w-full max-w-md overflow-hidden">
        
        <div className="flex justify-between items-center p-6 border-b border-gray-200 dark:border-gray-700 bg-gradient-to-r from-blue-50 to-purple-50 dark:from-gray-700 dark:to-gray-700">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white">Share Your Feedback</h2>
          <button
            onClick={handleSkipFeedback}
            className="p-1 hover:bg-white/50 dark:hover:bg-gray-600 rounded-lg transition-colors"
            title="Close feedback form"
          >
            <X className="h-5 w-5 text-gray-600 dark:text-gray-400" />
          </button>
        </div>

        <form onSubmit={handleFeedbackSubmit} className="p-6 space-y-4">
          
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Name *
            </label>
            <input
              type="text"
              name="name"
              value={feedbackData.name}
              onChange={handleFeedbackChange}
              required
              className="w-full px-3 py-2 bg-white dark:bg-gray-700 text-gray-900 dark:text-white border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Your name"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Email *
            </label>
            <input
              type="email"
              name="email"
              value={feedbackData.email}
              onChange={handleFeedbackChange}
              required
              className="w-full px-3 py-2 bg-white dark:bg-gray-700 text-gray-900 dark:text-white border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Your email"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Type of Feedback *
            </label>
            <select
              name="type"
              value={feedbackData.type}
              onChange={handleFeedbackChange}
              required
              className="w-full px-3 py-2 bg-white dark:bg-gray-700 text-gray-900 dark:text-white border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="event">Event</option>
              <option value="donation">Donation</option>
              <option value="giftpool">Gift Pool</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Rating *
            </label>
            <div className="flex gap-2">
              {[1, 2, 3, 4, 5].map((star) => (
                <button
                  key={star}
                  type="button"
                  onClick={() => handleFeedbackRating(star)}
                  className={`transition-all ${
                    feedbackData.rating >= star
                      ? 'text-yellow-400'
                      : 'text-gray-300 dark:text-gray-600'
                  }`}
                >
                  <Star className="h-6 w-6 fill-current" />
                </button>
              ))}
              <span className="ml-2 text-gray-600 dark:text-gray-400">
                {feedbackData.rating} / 5
              </span>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Your Feedback *
            </label>
            <textarea
              name="message"
              value={feedbackData.message}
              onChange={handleFeedbackChange}
              required
              rows="4"
              className="w-full px-3 py-2 bg-white dark:bg-gray-700 text-gray-900 dark:text-white border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
              placeholder="Tell us what you think..."
            />
          </div>

          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={handleSkipFeedback}
              className="flex-1 px-4 py-2 bg-gray-300 dark:bg-gray-600 text-gray-900 dark:text-white rounded-lg hover:bg-gray-400 dark:hover:bg-gray-500 transition-colors font-medium"
            >
              Skip
            </button>
            <button
              type="submit"
              disabled={isSubmittingFeedback}
              className="flex-1 flex items-center justify-center gap-2 px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors font-medium disabled:opacity-50"
            >
              {isSubmittingFeedback ? (
                <>
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                  <span>Submitting...</span>
                </>
              ) : (
                <>
                  <Send className="h-4 w-4" />
                  <span>Submit</span>
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default FeedbackModal;
